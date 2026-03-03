package com.adriano.journey.data.llm

import android.app.Application
import android.util.Log
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.ModelDownloader
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class LargeLanguageModelMediaPipe(
    private val context: Application,
    private val modelDownloader: ModelDownloader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LargeLanguageModel {

    private val llmInferenceDeferred = CompletableDeferred<LlmInference>()
    private var textEmbedderDeferred = CompletableDeferred<TextEmbedder>()
    private val scope = CoroutineScope(ioDispatcher)

    init {
        scope.launch {
            initializeLlm()
        }
    }

    private suspend fun initializeLlm() {
        coroutineScope {
            launch {
                try {
                    val taskOptions = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath("/data/local/tmp/llm/gemma3-1b-it-int4.task")
                        .setPreferredBackend(LlmInference.Backend.GPU)
                        .build()
                    llmInferenceDeferred.complete(
                        LlmInference.createFromOptions(
                            context,
                            taskOptions
                        )
                    )
                } catch (e: Exception) {
                    Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
                }
            }
            launch {
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
    }

    override suspend fun generateResponse(prompt: String): String = withContext(ioDispatcher) {
        val llmInference = llmInferenceDeferred.await()
        try {
            llmInference.generateResponse(prompt)
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Error generating response", e)
            "Error generating response: ${e.message}"
        }
    }

    override suspend fun generateVector(prompt: String): List<Float> = withContext(ioDispatcher) {
        val textEmbedder = textEmbedderDeferred.await()
        try {
            val result = textEmbedder.embed(prompt)
            result.embeddingResult().embeddings().get(0).floatEmbedding().toList()
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Error generating vector", e)
            emptyList()
        }
    }
}
