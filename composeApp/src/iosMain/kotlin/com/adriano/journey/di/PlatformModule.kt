package com.adriano.journey.di

import com.adriano.journey.IosModelDownloader
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.ModelDownloader
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<LargeLanguageModel> {
        object : LargeLanguageModel {
            override suspend fun generateResponse(prompt: String): String {
                return "MediaPipe LLM not yet implemented on iOS."
            }
        }
    }
    single<ModelDownloader> { IosModelDownloader() }
}
