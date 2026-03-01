package com.adriano.journey.presentation

sealed interface JourneyEntryIntent {
    data class UpdateText(val text: String) : JourneyEntryIntent
}
