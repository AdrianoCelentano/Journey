package com.adriano.journey.data.llm

import com.adriano.journey.data.LargeLanguageModel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class LargeLanguageModelGeminiRemote : LargeLanguageModel {

    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-3-flash-preview")

    override suspend fun generateResponse(prompt: String): String {
        return model.generateContent(prompt).text ?: ""
    }
}
