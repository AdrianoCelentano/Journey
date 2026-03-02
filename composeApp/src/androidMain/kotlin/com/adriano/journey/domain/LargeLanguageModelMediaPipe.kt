package com.adriano.journey.domain

import android.app.Application
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LargeLanguageModelMediaPipe(
    private val context: Application,
    private val modelDownloader: ModelDownloader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LargeLanguageModel {

    private var llmInference: LlmInference? = null
    private val scope = CoroutineScope(ioDispatcher)

    init {
        val isDebuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            // Push the model to your phone's local storage:
            // adb shell mkdir -p /data/local/tmp/llm/
            // adb push /Users/adrian/AndroidStudioProjects/Journey/gemma_model/src/main/assets/gemma-2b-it-gpu-int4.bin /data/local/tmp/llm/
            initializeLlm("/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin")
        } else {
            // Initialize on app startup asynchronously to avoid blocking the main thread
            scope.launch {
                modelDownloader.downloadState.collect { state ->
                    if (state is DownloadState.Downloaded && llmInference == null) {
                        initializeLlm(state.path)
                    }
                }
            }
        }
    }

    private fun initializeLlm(modelPath: String) {
        try {
            Log.d("LargeLanguageModel", "Initializing LlmInference with path: $modelPath")
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(1024)
                .build()
            llmInference = LlmInference.createFromOptions(context, options)
            Log.d("LargeLanguageModel", "LlmInference initialized successfully.")
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
        }
    }

    override suspend fun generateResponse(prompt: String): String = withContext(ioDispatcher) {
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
        val words = prompt.split(" ")
        words.map { it.hashCode().toFloat() }
    }
}
