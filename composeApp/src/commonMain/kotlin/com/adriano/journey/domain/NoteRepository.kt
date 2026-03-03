package com.adriano.journey.domain

interface NoteRepository {
    suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long)
}
