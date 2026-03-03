package com.adriano.journey.data.local

import com.adriano.journey.domain.NoteRepository
import com.google.gson.Gson

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {
        val entity = NoteEntity(
            content = content,
            vectorJson = Gson().toJson(vector),
            timestamp = timestamp,
        )
        noteDao.insertNote(entity)
    }
}
