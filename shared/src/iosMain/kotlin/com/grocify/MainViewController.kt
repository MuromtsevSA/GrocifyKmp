package com.grocify

import androidx.compose.ui.window.ComposeUIViewController
import com.grocify.di.initKoin
import com.grocify.presentation.ui.App

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (!koinInitialized) {
        initKoin()
        koinInitialized = true
    }
    App()
}
