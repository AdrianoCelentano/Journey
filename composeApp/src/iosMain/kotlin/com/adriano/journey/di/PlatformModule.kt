package com.adriano.journey.di

import com.adriano.journey.IosModelDownloader
import com.adriano.journey.domain.ModelDownloader
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<ModelDownloader> { IosModelDownloader() }
}
