package com.adriano.journey.data

interface LargeLanguageModel {
    suspend fun generateResponse(prompt: String): String
    suspend fun isSupported(): Boolean = true
}
