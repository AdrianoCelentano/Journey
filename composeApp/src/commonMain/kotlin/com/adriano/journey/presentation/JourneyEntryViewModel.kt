package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.NoteRepository
import com.adriano.journey.utils.getCurrentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JourneyEntryViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val largeLanguageModel: LargeLanguageModel,
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
            is JourneyEntryIntent.SaveNote -> saveNote()
        }
    }

    private fun saveNote() {
        val currentText = state.value.text
        if (currentText.isBlank()) return

        viewModelScope.launch {
            val vector = largeLanguageModel.generateVector(currentText)
            val timestamp = getCurrentTimeMillis()
            noteRepository.saveNote(currentText, vector, timestamp)
            onIntent(JourneyEntryIntent.UpdateText(""))
        }
    }
}
