package com.adriano.journey.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.adriano.journey.ui.theme.JourneyTheme
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    JourneyTheme {
        KoinContext {
            val nav = remember { mutableStateOf(BottomNavScreen.Add) }
            MainScreen(
                currentScreen = nav.value,
                onScreenSelected = { nav.value = it },
            )
        }
    }
}
