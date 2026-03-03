package com.adriano.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adriano.journey.presentation.JourneyEntryIntent
import com.adriano.journey.presentation.JourneyEntryViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            JourneyEntryScreen()
        }
    }
}

@Composable
fun JourneyEntryScreen(
    viewModel: JourneyEntryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Journey Entry", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.text,
            onValueChange = { viewModel.onIntent(JourneyEntryIntent.UpdateText(it)) },
            modifier = Modifier.fillMaxWidth().weight(1f),
            minLines = 5,
            placeholder = { Text("What's on your mind?") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.onIntent(JourneyEntryIntent.SaveNote) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.text.isNotBlank(),
        ) {
            Text("Save Note")
        }
    }
}
