package com.adriano.journey.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriano.journey.data.LlmProvider
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    llmProvider: LlmProvider = koinInject(),
) {
    var isLocal by remember { mutableStateOf(llmProvider.isLocalModelEnabled) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Use Local Model", style = MaterialTheme.typography.titleMedium)
                Text("Run the AI model on your device for privacy", style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = isLocal,
                onCheckedChange = {
                    isLocal = it
                    llmProvider.isLocalModelEnabled = it
                },
            )
        }
    }
}
