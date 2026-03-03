package com.adriano.journey.data.llm

import android.app.Application
import android.util.Log
import com.adriano.journey.data.LargeLanguageModel
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LargeLanguageModelMediaPipe(
    private val context: Application,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LargeLanguageModel {

    private val llmInferenceDeferred = CompletableDeferred<LlmInference>()
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
                .setPreferredBackend(LlmInference.Backend.GPU)
                .build()
            llmInferenceDeferred.complete(LlmInference.createFromOptions(context, taskOptions))
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
        }
    }

    override suspend fun generateResponse(prompt: String): String = withContext(ioDispatcher) {
        val inference = llmInferenceDeferred.await()
        try {
            inference.generateResponse(prompt)
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Error generating response", e)
            "Error generating response: ${e.message}"
        }
    }
}
