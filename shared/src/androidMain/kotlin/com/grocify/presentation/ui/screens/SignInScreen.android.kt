package com.grocify.presentation.ui.screens

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clerk.api.Clerk
import com.clerk.ui.auth.AuthView
import com.grocify.auth.AndroidDevSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ClerkGateState {
    Loading,
    Ready,
    Error,
}

@Composable
actual fun SignInScreen(
    onContinue: () -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val isDebugBuild = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    val packageName = context.packageName

    val scope = rememberCoroutineScope()
    val isInitialized by Clerk.isInitialized.collectAsState(initial = false)
    val user by Clerk.userFlow.collectAsState(initial = null)
    var gateState by remember { mutableStateOf(ClerkGateState.Loading) }

    LaunchedEffect(isInitialized) {
        if (isInitialized) {
            gateState = ClerkGateState.Ready
        }
    }

    LaunchedEffect(Unit) {
        delay(12_000)
        if (!isInitialized) {
            gateState = ClerkGateState.Error
        }
    }

    LaunchedEffect(user) {
        if (user != null) {
            onContinue()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Grocify",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign in to sync your grocery list",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (gateState) {
                ClerkGateState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Connecting to Clerk…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                ClerkGateState.Error -> {
                    ClerkErrorCard(
                        packageName = packageName,
                        onRetry = {
                            gateState = ClerkGateState.Loading
                            scope.launch {
                                Clerk.reinitialize()
                                delay(12_000)
                                if (!Clerk.isInitialized.value) {
                                    gateState = ClerkGateState.Error
                                }
                            }
                        },
                        onDevBypass = if (isDebugBuild) {
                            {
                                AndroidDevSession.enableBypass()
                                onContinue()
                            }
                        } else {
                            null
                        },
                    )
                }

                ClerkGateState.Ready -> {
                    if (user == null) {
                        AuthView()
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ClerkErrorCard(
    packageName: String,
    onRetry: () -> Unit,
    onDevBypass: (() -> Unit)?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Clerk не подключился",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = "В Clerk Dashboard откройте Configure → Native applications и:\n" +
                    "1. Включите Native API\n" +
                    "2. Добавьте Android app с package name:\n$packageName\n" +
                    "3. Перезапустите приложение",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Text("Повторить")
            }
            if (onDevBypass != null) {
                OutlinedButton(
                    onClick = onDevBypass,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Продолжить без входа (debug)")
                }
            }
        }
    }
}
