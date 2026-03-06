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
        timestamp: Long,
    ) {
        val entity = NoteEntity(
            content = content,
            contentVector = vector,
            timestamp = timestamp,
        )
        noteBox.put(entity)
    }

    override suspend fun loadNotes(): List<Note> {
        return noteBox.all.toNotes()
    }

    override suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note> {
        val query = noteBox.query(NoteEntity_.contentVector.nearestNeighbors(queryVector, 5)).build()
        val results = query.findWithScores()
        return results.map { it.get().toNote() }
    }

    private fun NoteEntity.toNote(): Note {
        return Note(
            id = id.toInt(),
            content = content,
            contentVector = contentVector,
            timestamp = timestamp,
        )
    }

    private fun List<NoteEntity>.toNotes(): List<Note> {
        return map { it.toNote() }
    }
}
