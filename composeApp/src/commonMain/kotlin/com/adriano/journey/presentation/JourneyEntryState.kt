package com.adriano.journey.presentation

data class JourneyEntryState(
    val noteInput: String = "",
    val searchInput: String = "",
    val questions: List<Question> = emptyList(),
    val startDate: Long? = null,
    val endDate: Long? = null,
    val searchLoading: Boolean = false,
    val addNoteLoading: Boolean = false,
)

data class Question(
    val question: String,
    val answer: String,
)
