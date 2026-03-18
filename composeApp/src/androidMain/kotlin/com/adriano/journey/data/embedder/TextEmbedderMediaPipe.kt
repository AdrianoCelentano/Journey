package com.adriano.journey.data.embedder

import android.app.Application
import android.util.Log
import com.adriano.journey.data.JourneyTextEmbedder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextEmbedderMediaPipe(
    private val context: Application,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : JourneyTextEmbedder {

    private var textEmbedderDeferred = CompletableDeferred<TextEmbedder>()
    private val scope = CoroutineScope(defaultDispatcher)

    init {
        scope.launch {
            try {
                val baseOptions = BaseOptions.builder()
                    .setModelAssetPath("/data/local/tmp/llm/universal_sentence_encoder.tflite")
                    .build()

                val options = TextEmbedderOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setQuantize(false)
                    .build()

                textEmbedderDeferred.complete(TextEmbedder.createFromOptions(context, options))
            } catch (e: Exception) {
                Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
            }
        }
    }

    override suspend fun generateVector(prompt: String): FloatArray = withContext(defaultDispatcher) {
        val textEmbedder = textEmbedderDeferred.await()
        try {
            val result = textEmbedder.embed(prompt)
            result.embeddingResult().embeddings().get(0).floatEmbedding()
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Error generating vector", e)
            floatArrayOf()
        }
    }
}
