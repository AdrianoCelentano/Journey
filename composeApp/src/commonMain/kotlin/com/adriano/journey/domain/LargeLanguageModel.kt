package com.adriano.journey.domain

interface LargeLanguageModel {
    suspend fun generateResponse(prompt: String): String
}
