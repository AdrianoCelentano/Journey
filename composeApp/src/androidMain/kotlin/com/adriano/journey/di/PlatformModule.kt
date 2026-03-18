package com.adriano.journey.di

import androidx.room.Room.databaseBuilder
import com.adriano.journey.data.AndroidAppPreferences
import com.adriano.journey.data.AppPreferences
import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.NoteRepository
import com.adriano.journey.data.db.objectbox.MyObjectBox
import com.adriano.journey.data.db.room.AppDatabase
import com.adriano.journey.data.db.room.NoteDao
import com.adriano.journey.data.db.room.NoteRepositoryRoom
import com.adriano.journey.data.embedder.TextEmbedderMediaPipe
import com.adriano.journey.data.llm.LargeLanguageModelGeminiNano
import com.adriano.journey.data.llm.LargeLanguageModelGeminiRemote
import com.adriano.journey.data.llm.LargeLanguageModelMediaPipe
import io.objectbox.BoxStore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AppPreferences> { AndroidAppPreferences(androidContext()) }
    single<LargeLanguageModel>(named("local_nano")) { LargeLanguageModelGeminiNano() }
    single<LargeLanguageModel>(named("local_fallback")) { LargeLanguageModelMediaPipe(androidApplication()) }
    single<LargeLanguageModel>(named("remote")) {
        LargeLanguageModelGeminiRemote()
    }
    single<JourneyTextEmbedder>(createdAtStart = true) {
        TextEmbedderMediaPipe(androidApplication())
//        TextEmbedderRemote()
    }
    single<AppDatabase> {
        databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "journey_db",
        ).build()
    }
    single<BoxStore> {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
    single<NoteDao> { get<AppDatabase>().noteDao() }
    single<NoteRepository> { NoteRepositoryRoom(get()) }
}
