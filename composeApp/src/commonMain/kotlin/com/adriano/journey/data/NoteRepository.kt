package com.adriano.journey.data

import com.adriano.journey.domain.Note

interface NoteRepository {
    suspend fun saveNote(content: String, vector: FloatArray, timestamp: Long)
    suspend fun loadNotes(): List<Note>
    suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note>
}
