package com.adriano.journey.domain

data class Note(
    val id: Int,
    val content: String,
    val contentVector: List<Float>,
    val timestamp: Long,
)
