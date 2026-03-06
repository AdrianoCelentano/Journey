package com.adriano.journey.data.llm

import com.adriano.journey.BuildConfig
import com.adriano.journey.data.JourneyTextEmbedder
import com.google.genai.Client
import com.google.genai.types.EmbedContentResponse
import com.google.mlkit.genai.prompt.GenerateContentResponse

class TextEmbedderGeminiNano : JourneyTextEmbedder {

    private val client = Client.builder().apiKey(BuildConfig.GEMINI_API_KEY).build()

    override suspend fun generateVector(prompt: String): FloatArray {
        return try {
            client.models.embedContent(
                "text-embedding-004",
                prompt,
                null
            ).embedding
        } catch (e: Exception) {
            floatArrayOf()
        }
    }
}

private val EmbedContentResponse.embedding
    get() = embeddings().get()
        .firstOrNull()?.values()?.get()
        ?.toFloatArray() ?: floatArrayOf()
