package com.adriano.journey.di

import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.LargeLanguageModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<LargeLanguageModel>(createdAtStart = true) {
        LargeLanguageModelImpl(androidContext())
    }
}
