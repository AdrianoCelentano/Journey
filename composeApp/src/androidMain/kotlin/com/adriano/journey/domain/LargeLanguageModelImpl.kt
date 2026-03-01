package com.adriano.journey.domain

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class LargeLanguageModelImpl(
    private val context: Context,
    private val modelName: String = "gemma-2b-it-cpu-int8.bin",
) : LargeLanguageModel {

    private var llmInference: LlmInference? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Initialize on app startup asynchronously to avoid blocking the main thread
        scope.launch {
            initializeLlm()
        }
    }

    private fun initializeLlm() {
        try {
            val modelFile = File(context.filesDir, modelName)
            if (!modelFile.exists()) {
                Log.d("LargeLanguageModel", "Copying model from assets to internal storage...")
                copyModelFromAssets(modelFile)
                Log.d("LargeLanguageModel", "Model copied successfully.")
            }

            if (modelFile.exists()) {
                Log.d("LargeLanguageModel", "Initializing LlmInference...")
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelFile.absolutePath)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
                Log.d("LargeLanguageModel", "LlmInference initialized successfully.")
            }
        } catch (e: Exception) {
            Log.e("LargeLanguageModel", "Failed to initialize LLM", e)
        }
    }

    private fun copyModelFromAssets(destination: File) {
        context.assets.open(modelName).use { inputStream ->
            FileOutputStream(destination).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    override suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
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
}
