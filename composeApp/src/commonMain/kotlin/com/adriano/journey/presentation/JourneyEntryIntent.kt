package com.adriano.journey.presentation

sealed interface JourneyEntryIntent {
    data class UpdateNoteText(val text: String) : JourneyEntryIntent
    data class UpdateNoteSearchText(val text: String) : JourneyEntryIntent
    object SaveNote : JourneyEntryIntent
    object SearchNotes : JourneyEntryIntent
    data class UpdateDateRange(val startDate: Long?, val endDate: Long?) : JourneyEntryIntent
}
