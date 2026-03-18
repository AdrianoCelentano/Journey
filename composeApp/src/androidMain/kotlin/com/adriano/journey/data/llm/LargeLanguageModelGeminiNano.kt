package com.adriano.journey.data.llm

import com.adriano.journey.data.LargeLanguageModel
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerateContentResponse
import com.google.mlkit.genai.prompt.Generation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LargeLanguageModelGeminiNano : LargeLanguageModel {

    companion object {
        var _isSupported: Boolean = false

        init {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val status = Generation.getClient().checkStatus()
                    _isSupported = status != FeatureStatus.UNAVAILABLE
                } catch (e: Exception) {
                    _isSupported = false
                }
            }
        }

        fun isSupported(): Boolean = _isSupported
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
