package com.adriano.journey.data

interface JourneyTextEmbedder {
    suspend fun generateVector(prompt: String): FloatArray
}
