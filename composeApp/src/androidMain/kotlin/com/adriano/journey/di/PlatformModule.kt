package com.adriano.journey.di

import com.adriano.journey.AndroidModelDownloader
import com.adriano.journey.domain.ModelDownloader
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<ModelDownloader> { AndroidModelDownloader(androidContext()) }
}
