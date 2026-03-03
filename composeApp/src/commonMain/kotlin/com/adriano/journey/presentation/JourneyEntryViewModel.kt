package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.journey.domain.JourneyNotesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JourneyEntryViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val journeyEntryService: JourneyNotesService,
) : ViewModel() {

    companion object {
        private const val TEXT_KEY = "journey_entry_text"
    }

    private val _state = MutableStateFlow(
        JourneyEntryState(
            note = savedStateHandle.get<String>(TEXT_KEY) ?: "",
        ),
    )
    val state: StateFlow<JourneyEntryState> = _state.asStateFlow()

    fun onIntent(intent: JourneyEntryIntent) {
        when (intent) {
            is JourneyEntryIntent.UpdateNoteText -> {
                savedStateHandle[TEXT_KEY] = intent.text
                _state.update { it.copy(note = intent.text) }
            }
            is JourneyEntryIntent.SaveNote -> saveNote()
            is JourneyEntryIntent.SearchNotes -> searchNotes()
            is JourneyEntryIntent.UpdateNoteSearchText -> {
                savedStateHandle[TEXT_KEY] = intent.text
                _state.update { it.copy(search = intent.text) }
            }
            is JourneyEntryIntent.UpdateDateRange -> {
                _state.update { it.copy(startDate = intent.startDate, endDate = intent.endDate) }
            }
        }
    }

    private fun searchNotes() {
        viewModelScope.launch {
            val answer = journeyEntryService.searchEntries(state.value.search)
            _state.update { it.copy(answer = answer) }
        }
    }

    private fun saveNote() {
        val currentText = state.value.note
        if (currentText.isBlank()) return

        viewModelScope.launch {
            journeyEntryService.addEntry(currentText)
            onIntent(JourneyEntryIntent.UpdateNoteText(""))
        }
    }
}
