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
            noteInput = savedStateHandle.get<String>(TEXT_KEY) ?: "",
        ),
    )
    val state: StateFlow<JourneyEntryState> = _state.asStateFlow()

    fun onIntent(intent: JourneyEntryIntent) {
        when (intent) {
            is JourneyEntryIntent.UpdateNoteText -> {
                savedStateHandle[TEXT_KEY] = intent.text
                _state.update { it.copy(noteInput = intent.text) }
            }

            is JourneyEntryIntent.SaveNote -> saveNote()
            is JourneyEntryIntent.SearchNotes -> searchNotes()
            is JourneyEntryIntent.UpdateNoteSearchText -> {
                savedStateHandle[TEXT_KEY] = intent.text
                _state.update { it.copy(searchInput = intent.text) }
            }

            is JourneyEntryIntent.UpdateDateRange -> {
                _state.update { it.copy(startDate = intent.startDate, endDate = intent.endDate) }
            }

            JourneyEntryIntent.EnhanceNote -> enhanceNote()
        }
    }

    private fun enhanceNote() {
        viewModelScope.launch {
            _state.update { it.copy(enhanceNoteLoading = true) }
            try {
                val note = journeyEntryService.enhanceNote(state.value.noteInput)
                _state.update { it.copy(noteInput = note) }
            } finally {
                _state.update { it.copy(enhanceNoteLoading = false) }
            }
        }
    }

    private fun searchNotes() {
        viewModelScope.launch {
            _state.update { it.copy(searchLoading = true) }
            try {
                val answer = journeyEntryService.searchEntries(state.value.searchInput)
                _state.update {
                    it.copy(
                        questions = _state.value.questions.plus(
                            Question(
                                state.value.searchInput,
                                answer,
                            ),
                        ),
                        searchInput = "",
                    )
                }
            } finally {
                _state.update { it.copy(searchLoading = false) }
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            val textToSave = state.value.noteInput
            if (textToSave.isBlank()) {
                _state.update { it.copy(noteInput = "") }
                return@launch
            }
            _state.update { it.copy(addNoteLoading = true, noteInput = "") }
            try {
                journeyEntryService.addEntry(textToSave)
            } finally {
                _state.update { it.copy(addNoteLoading = false) }
            }
        }
    }

    public inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        while (true) {
            val prevValue = value
            val nextValue = function(prevValue)
            println("new state $nextValue")
            if (compareAndSet(prevValue, nextValue)) {
                return
            }
        }
    }
}
