package com.adriano.journey.domain

data class Note(
    val id: Int,
    val content: String,
    val contentVector: FloatArray,
    val timestamp: Long,
)
