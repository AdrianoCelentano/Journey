package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class JourneyEntryViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TEXT_KEY = "journey_entry_text"
    }

    private val _state = MutableStateFlow(
        JourneyEntryState(
            text = savedStateHandle.get<String>(TEXT_KEY) ?: "",
        ),
    )
    val state: StateFlow<JourneyEntryState> = _state.asStateFlow()

    fun onIntent(intent: JourneyEntryIntent) {
        when (intent) {
            is JourneyEntryIntent.UpdateText -> {
                savedStateHandle[TEXT_KEY] = intent.text
                _state.update { it.copy(text = intent.text) }
            }
        }
    }
}
