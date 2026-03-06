package com.adriano.journey.data.db.room

import com.adriano.journey.data.NoteRepository
import com.adriano.journey.domain.Note
import kotlin.math.sqrt

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {

    override suspend fun saveNote(content: String, vector: FloatArray, timestamp: Long) {
        val entity = NoteEntity(
            content = content,
            contentVector = vector,
            timestamp = timestamp,
        )
        noteDao.insertNote(entity)
    }

    override suspend fun loadNotes(): List<Note> {
        val notes = noteDao.getAllNotes()
        return notes.toNotes()
    }

    override suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note> {
        val notes = loadNotes()
        return findMatchingNotes(notes, queryVector)
    }

    private fun findMatchingNotes(
        notes: List<Note>,
        queryVector: FloatArray,
    ): List<Note> = notes
        .map { note ->
            val score = queryVector.cosineSimilarity(note.contentVector)
            note to score
        }
        .sortedByDescending { it.second }
        .take(5)
        .map { it.first }

    fun FloatArray.cosineSimilarity(other: FloatArray): Float {
        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f

        for (i in this.indices) {
            dotProduct += this[i] * other[i]
            normA += this[i] * this[i]
            normB += other[i] * other[i]
        }

        return if (normA == 0.0f || normB == 0.0f) {
            0.0f
        } else {
            (dotProduct / (sqrt(normA) * sqrt(normB)))
        }
    }

    private fun List<NoteEntity>.toNotes(): List<Note> {
        return map { entity ->
            Note(
                id = entity.id,
                content = entity.content,
                contentVector = entity.contentVector,
                timestamp = entity.timestamp,
            )
        }
    }

}