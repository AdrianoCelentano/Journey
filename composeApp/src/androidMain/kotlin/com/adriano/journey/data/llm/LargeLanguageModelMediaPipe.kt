package com.adriano.journey.data.llm

import android.app.Application
import android.util.Log
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.ModelDownloader
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class LargeLanguageModelMediaPipe(
    private val context: Application,
    private val modelDownloader: ModelDownloader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LargeLanguageModel {

    private var llmInference: LlmInference? = null
    private var textEmbedder: TextEmbedder? = null
    private val scope = CoroutineScope(ioDispatcher)

    init {
        scope.launch {
            initializeLlm()
        }
    }

    private fun initializeLlm() {
        try {
            val taskOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath("/data/local/tmp/llm/gemma3-1b-it-int4.task")
//                .setMaxTokens(1024)
//                .setMaxTopK(40)
                .setPreferredBackend(LlmInference.Backend.GPU)
                .build()
            llmInference = LlmInference.createFromOptions(context, taskOptions)

            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("/data/local/tmp/llm/embeddinggemma-300M_seq512_mixed-precision.tflite")
                .build()

            val options = TextEmbedderOptions.builder()
                .setBaseOptions(baseOptions)
                .setQuantize(false)
                .build()

            textEmbedder = TextEmbedder.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
        }
    }

    override suspend fun generateResponse(prompt: String): String = withContext(ioDispatcher) {
        while (llmInference == null) {
            delay(200.milliseconds)
        }
        val inference = llmInference
        if (inference != null) {
            try {
                inference.generateResponse(prompt)
            } catch (e: Exception) {
                Log.e("LargeLanguageModel", "Error generating response", e)
                "Error generating response: ${e.message}"
            }
        } else {
            "Error: LLM not initialized yet. Please wait."
        }
    }

    override suspend fun generateVector(prompt: String): List<Float> = withContext(ioDispatcher) {
        while (textEmbedder == null) {
            delay(200.milliseconds)
        }
        val embedder = textEmbedder
        if (embedder != null) {
            try {
                val result = embedder.embed(prompt)
                result.embeddingResult().embeddings().get(0).floatEmbedding().toList()
            } catch (e: Exception) {
                Log.e("LargeLanguageModel", "Error generating vector", e)
                emptyList()
            }
        } else {
            Log.e("LargeLanguageModel", "TextEmbedder not initialized")
            emptyList()
        }
    }
}
