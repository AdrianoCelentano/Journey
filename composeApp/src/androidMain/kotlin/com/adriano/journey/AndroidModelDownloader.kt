package com.adriano.journey

import android.content.Context
import com.adriano.journey.domain.DownloadState
import com.adriano.journey.domain.ModelDownloader
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidModelDownloader(context: Context) : ModelDownloader {

    private val assetPackManager = AssetPackManagerFactory.getInstance(context)
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    override val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    init {
        assetPackManager.registerListener(downloadStateListener())
        checkCurrentState()
    }

    override fun downloadModel() {
        val path = getModelPath()
        if (path != null) {
            _downloadState.value = DownloadState.Downloaded(path)
            return
        }
        assetPackManager.fetch(listOf(packName))
    }

    private fun getModelPath(): String? {
        val packLocation = assetPackManager.getPackLocation(packName)
        val assetsPath = packLocation?.assetsPath() ?: return null
        return "$assetsPath/gemma-2b-it-gpu-int4.bin"
    }

    private fun checkCurrentState() {
        val path = getModelPath()
        if (path != null) {
            _downloadState.value = DownloadState.Downloaded(path)
        }
    }

    private fun downloadStateListener(): AssetPackStateUpdateListener =
        AssetPackStateUpdateListener { state: AssetPackState ->
            when (state.status()) {
                AssetPackStatus.PENDING, AssetPackStatus.DOWNLOADING, AssetPackStatus.TRANSFERRING -> {
                    _downloadState.value = DownloadState.Downloading(state.progress)
                }

                AssetPackStatus.COMPLETED -> {
                    val path = getModelPath()
                    if (path != null) {
                        _downloadState.value = DownloadState.Downloaded(path)
                    } else {
                        _downloadState.value =
                            DownloadState.Error("Model downloaded but path not found")
                    }
                }

                AssetPackStatus.FAILED -> {
                    _downloadState.value =
                        DownloadState.Error("Download failed with error code ${state.errorCode()}")
                }

                AssetPackStatus.CANCELED -> {
                    _downloadState.value = DownloadState.Idle
                }

                AssetPackStatus.UNKNOWN -> {
                    _downloadState.value = DownloadState.Error("Unknown download state")
                }
            }
        }

    private val AssetPackState.progress: Float
        get() = if (totalBytesToDownload() == 0L) 0f
        else bytesDownloaded().toFloat() / totalBytesToDownload().toFloat()

    companion object {
        private const val packName = "gemma_model"
    }

}
