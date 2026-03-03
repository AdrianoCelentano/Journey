package com.adriano.journey.data.db

import com.adriano.journey.data.NoteRepository
import com.adriano.journey.domain.Note

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {
        val entity = NoteEntity(
            content = content,
            contentVector = vector.toFloatArray(),
            timestamp = timestamp,
        )
        noteDao.insertNote(entity)
    }

    override suspend fun loadNotes(): List<Note> {
        val notes = noteDao.getAllNotes()
        return notes.toNotes()
    }
}

private fun List<NoteEntity>.toNotes(): List<Note> {
    return map { entity ->
        Note(
            id = entity.id,
            content = entity.content,
            contentVector = entity.contentVector.toList(),
            timestamp = entity.timestamp,
        )
    }
}
