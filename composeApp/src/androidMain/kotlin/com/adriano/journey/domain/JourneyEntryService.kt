package com.adriano.journey.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JourneyEntryService(
    private val llm: LargeLanguageModel,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val scope = CoroutineScope(ioDispatcher)

    fun addEntry(note: String) = scope.launch {

        val correctedNote = llm.generateResponse(prompt(note))

    }

    private fun prompt(note: String): String =
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
}