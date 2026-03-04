package com.adriano.journey.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class BottomNavScreen(val title: String) {
    Add("Add"),
    Search("Search"),
    Settings("Settings"),
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    currentScreen: BottomNavScreen = BottomNavScreen.Add,
    onScreenSelected: (BottomNavScreen) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            JourneyBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = onScreenSelected,
            )
        },
    ) { paddingValues ->
        val modifier = Modifier
            .padding(paddingValues)
            .imePadding()
        when (currentScreen) {
            BottomNavScreen.Add -> AddScreen(modifier)
            BottomNavScreen.Search -> SearchScreen(modifier)
            BottomNavScreen.Settings -> SettingsScreen(modifier)
        }
    }
}

@Composable
fun JourneyBottomBar(
    currentScreen: BottomNavScreen,
    onScreenSelected: (BottomNavScreen) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Add,
            onClick = { onScreenSelected(BottomNavScreen.Add) },
            icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
            label = { Text("Add") },
        )
        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Search,
            onClick = { onScreenSelected(BottomNavScreen.Search) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
        )
        NavigationBarItem(
            selected = currentScreen == BottomNavScreen.Settings,
            onClick = { onScreenSelected(BottomNavScreen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
        )
    }
}
