package com.grocify.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grocify.auth.IosClerkBridge

/**
 * On iOS, real Clerk UI lives in the Swift shell ([ContentView]).
 * This screen waits for [IosClerkBridge] session or offers a debug bypass.
 */
@Composable
actual fun SignInScreen(
    onContinue: () -> Unit,
    modifier: Modifier,
) {
    var signedIn by remember { mutableStateOf(IosClerkBridge.isSignedIn()) }

    DisposableEffect(Unit) {
        val listener = {
            signedIn = IosClerkBridge.isSignedIn()
        }
        IosClerkBridge.addSessionListener(listener)
        onDispose { IosClerkBridge.removeSessionListener(listener) }
    }

    LaunchedEffect(signedIn) {
        if (signedIn) {
            onContinue()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Grocify",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ожидание входа через Clerk (Swift)…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = {
                IosClerkBridge.enableDevBypass()
                onContinue()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Продолжить без входа (debug)")
        }
    }
}
