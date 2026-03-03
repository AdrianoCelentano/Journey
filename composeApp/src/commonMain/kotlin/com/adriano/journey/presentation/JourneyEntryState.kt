package com.adriano.journey.presentation

data class JourneyEntryState(
    val note: String = "",
    val search: String = "",
    val answer: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
)
