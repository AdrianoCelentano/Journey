package com.adriano.journey.data

import com.adriano.journey.domain.Note

interface NoteRepository {
    suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long)
    suspend fun loadNotes(): List<Note>
}
