package com.grocify.presentation.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SignInScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
)
