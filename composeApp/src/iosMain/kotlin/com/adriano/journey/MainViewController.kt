package com.adriano.journey

import androidx.compose.ui.window.ComposeUIViewController
import com.adriano.journey.di.initKoin
import com.adriano.journey.ui.App

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
