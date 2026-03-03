package com.adriano.journey.domain

import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.NoteRepository
import com.adriano.journey.utils.getCurrentTimeMillis
import kotlin.math.sqrt

class JourneyNotesService(
    private val Llm: LargeLanguageModel,
    private val textEmbedder: JourneyTextEmbedder,
    private val noteRepository: NoteRepository,
) {
    suspend fun addEntry(note: String) {
        val correctedNote = Llm.generateResponse(saveNotePrompt(note))
        val vector = textEmbedder.generateVector(correctedNote)
        val timestamp = getCurrentTimeMillis()
        noteRepository.saveNote(correctedNote, vector, timestamp)
    }

    private fun saveNotePrompt(note: String): String =
        """You are a strict text editor. Your task is to rewrite the following rough note into grammatically correct, complete sentences.
    
    CRITICAL RULES:
    1. Do NOT add any new ideas, details, or facts.
    2. Preserve the original keywords and meaning exactly.
    3. Only fix spelling, grammar, and expand fragments into full sentences.
    4. Output ONLY the corrected text. Do not include any introductory or concluding remarks.
    5. keep the original language
    
    Rough Note:
    $note
    
    Corrected Note:"""

    suspend fun searchEntries(search: String): String {
        val notes = noteRepository.loadNotes()
        val queryVector = textEmbedder.generateVector(search)
        val matchingNotes = findMatchingNotes(notes, queryVector).map { it.content }
        val prompt = searchNotePrompt(matchingNotes, search)
        return Llm.generateResponse(prompt)
    }

    private fun searchNotePrompt(notes: List<String>, search: String): String =
        """You are a helpful assistant. Your task is to answer the user's question using ONLY the provided notes.

        CRITICAL RULES:
        1. Base your answer strictly on the information in the notes below. Do not use outside knowledge.
        2. If the answer cannot be found in the notes, do not guess. Simply reply with: "I cannot find the answer to this in your notes."
        3. Keep your answer concise, direct, and easy to read.
        
        NOTES:
        $notes
        
        QUESTION: $search
        
        ANSWER:"""

    private fun findMatchingNotes(
        notes: List<Note>,
        queryVector: List<Float>,
    ): List<Note> = notes
        .map { note ->
            val score = queryVector.cosineSimilarity(note.contentVector.toFloatArray())
            note to score
        }
        .sortedByDescending { it.second }
        .take(5)
        .map { it.first }

    fun List<Float>.cosineSimilarity(other: FloatArray): Float {
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
}
