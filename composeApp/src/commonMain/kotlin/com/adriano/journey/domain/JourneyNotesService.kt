package com.adriano.journey.domain

import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LlmProvider
import com.adriano.journey.data.NoteRepository
import com.adriano.journey.utils.getCurrentTimeMillis

class JourneyNotesService(
    private val llmProvider: LlmProvider,
    private val textEmbedder: JourneyTextEmbedder,
    private val noteRepository: NoteRepository,
) {

    private val llm get() = llmProvider.provide()

    suspend fun addEntry(note: String) {
        val vector = textEmbedder.generateVector(note)
        val timestamp = getCurrentTimeMillis()
        noteRepository.saveNote(note, vector, timestamp)
    }

    suspend fun enhanceNote(note: String): String {
        return llm.generateResponse(enhanceNotePrompt(note))
    }

    private fun enhanceNotePrompt(note: String): String =
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
        val queryVector = textEmbedder.generateVector(search)
        val notes = noteRepository.loadMatchingNotes(queryVector)
        val notesContents = notes.map { it.content }
        val prompt = searchNotePrompt(notesContents, search)
        return llm.generateResponse(prompt)
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
}
