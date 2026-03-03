package com.adriano.journey.presentation

sealed interface JourneyEntryIntent {
    data class UpdateNoteText(val text: String) : JourneyEntryIntent
    data class UpdateNoteSearchText(val text: String) : JourneyEntryIntent
    object SaveNote : JourneyEntryIntent
    object SearchNotes : JourneyEntryIntent
}
