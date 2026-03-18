package com.adriano.journey.data.llm

import com.adriano.journey.data.LargeLanguageModel
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerateContentResponse
import com.google.mlkit.genai.prompt.Generation
import kotlinx.coroutines.flow.first

class LargeLanguageModelGeminiNano : LargeLanguageModel {

    override suspend fun isSupported(): Boolean {
        return try {
            Generation.getClient().checkStatus() != FeatureStatus.UNAVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    private val generativeModel = Generation.getClient()

    override suspend fun generateResponse(prompt: String): String {
        val status = generativeModel.checkStatus()

        if (status == FeatureStatus.DOWNLOADABLE || status == FeatureStatus.DOWNLOADING) {
            generativeModel.download().first { downloadStatus ->
                downloadStatus is DownloadStatus.DownloadCompleted || downloadStatus is DownloadStatus.DownloadFailed
            }.also {
                if (it is DownloadStatus.DownloadFailed) {
                    return ""
                }
            }
        } else if (status != FeatureStatus.AVAILABLE) {
            return ""
        }

        return generativeModel.generateContent(prompt).text
    }
}

private val GenerateContentResponse.text get() = candidates.firstOrNull()?.text ?: ""
