package com.adriano.journey.domain

import kotlinx.coroutines.flow.StateFlow

sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(val progress: Float = 0f) : DownloadState
    data class Downloaded(val path: String) : DownloadState
    data class Error(val message: String) : DownloadState
}

interface ModelDownloader {
    val downloadState: StateFlow<DownloadState>
    fun downloadModel()
}
