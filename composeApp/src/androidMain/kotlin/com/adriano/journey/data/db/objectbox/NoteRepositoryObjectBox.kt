package com.adriano.journey.data.db.objectbox

import com.adriano.journey.data.NoteRepository
import com.adriano.journey.domain.Note
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

class NoteRepositoryObjectBox(val store: BoxStore) : NoteRepository {

    val noteBox = store.boxFor(NoteEntity::class)

    override suspend fun saveNote(
        content: String,
        vector: FloatArray,
        timestamp: Long
    ) {
        // Implementation for ObjectBox
    }

    override suspend fun loadNotes(): List<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note> {
        TODO("Not yet implemented")
    }
}
