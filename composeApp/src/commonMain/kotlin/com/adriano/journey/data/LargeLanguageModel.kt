package com.adriano.journey.data

interface LargeLanguageModel {
    suspend fun generateResponse(prompt: String): String
}
