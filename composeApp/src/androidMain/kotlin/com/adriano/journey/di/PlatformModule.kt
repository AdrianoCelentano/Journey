package com.adriano.journey.di

import com.adriano.journey.data.llm.AndroidModelDownloader
import com.adriano.journey.data.llm.LargeLanguageModelMediaPipe
import com.adriano.journey.data.local.AppDatabase
import com.adriano.journey.data.local.NoteDao
import com.adriano.journey.data.local.NoteRepositoryImpl
import com.adriano.journey.domain.JourneyEntryService
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.ModelDownloader
import com.adriano.journey.domain.NoteRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<ModelDownloader> { AndroidModelDownloader(androidContext()) }
    single<JourneyEntryService> { JourneyEntryService(get()) }
    single<LargeLanguageModel>(createdAtStart = true) {
        LargeLanguageModelMediaPipe(androidApplication(), get())
    }
    single<AppDatabase> {
        androidx.room.Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "journey_db",
        ).build()
    }
    single<NoteDao> { get<AppDatabase>().noteDao() }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
}
