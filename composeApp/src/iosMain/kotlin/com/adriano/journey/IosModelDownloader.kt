package com.adriano.journey

import com.adriano.journey.data.DownloadState
import com.adriano.journey.data.ModelDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosModelDownloader : ModelDownloader {
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Error("Not supported on iOS"))
    override val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    override fun downloadModel() {
        // No-op for iOS
    }
}
