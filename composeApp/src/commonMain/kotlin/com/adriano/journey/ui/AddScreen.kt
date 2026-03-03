package com.adriano.journey.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriano.journey.presentation.JourneyEntryIntent
import com.adriano.journey.presentation.JourneyEntryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddScreen(modifier: Modifier = Modifier, viewModel: JourneyEntryViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("New Entry", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.note,
            onValueChange = { viewModel.onIntent(JourneyEntryIntent.UpdateNoteText(it)) },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("What's on your mind?") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.onIntent(JourneyEntryIntent.SaveNote) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save Note")
        }
    }
}
