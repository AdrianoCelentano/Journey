package com.adriano.journey.data.local

import com.adriano.journey.domain.NoteRepository

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {
        val entity = NoteEntity(
            content = content,
            contentVector = vector.toFloatArray(),
            timestamp = timestamp,
        )
        noteDao.insertNote(entity)
    }
}
