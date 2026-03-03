package com.adriano.journey.di

import com.adriano.journey.IosModelDownloader
import com.adriano.journey.data.AppPreferences
import com.adriano.journey.data.IosAppPreferences
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.ModelDownloader
import com.adriano.journey.data.NoteRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AppPreferences> { IosAppPreferences() }
    single<LargeLanguageModel>(named("local")) {
        object : LargeLanguageModel {
            override suspend fun generateResponse(prompt: String): String {
                return "MediaPipe LLM not yet implemented on iOS."
            }
        }
    }
    single<LargeLanguageModel>(named("remote")) {
        object : LargeLanguageModel {
            override suspend fun generateResponse(prompt: String): String {
                return "Remote LLM not yet implemented on iOS."
            }
        }
    }
    single<ModelDownloader> { IosModelDownloader() }
    single<NoteRepository> {
        object : NoteRepository {
            override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {}
        }
    }
}
