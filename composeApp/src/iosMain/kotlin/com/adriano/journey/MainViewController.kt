package com.adriano.journey

import androidx.compose.ui.window.ComposeUIViewController
import com.adriano.journey.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
