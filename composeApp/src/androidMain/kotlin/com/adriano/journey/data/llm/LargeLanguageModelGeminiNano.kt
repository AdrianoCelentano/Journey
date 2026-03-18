package com.adriano.journey.data.llm

import com.adriano.journey.data.LargeLanguageModel
import com.google.mlkit.genai.prompt.GenerateContentResponse
import com.google.mlkit.genai.prompt.Generation

class LargeLanguageModelGeminiNano : LargeLanguageModel {

    val generativeModel = Generation.getClient()

    override suspend fun generateResponse(prompt: String): String {
        return generativeModel.generateContent(prompt).text
    }
}

private val GenerateContentResponse.text get() = candidates.firstOrNull()?.text ?: ""
