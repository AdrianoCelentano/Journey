package com.adriano.journey.di

import com.adriano.journey.data.LlmProvider
import com.adriano.journey.domain.JourneyNotesService
import com.adriano.journey.presentation.JourneyEntryViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single { com.adriano.journey.data.LlmProvider() }
    single { JourneyNotesService(get<LlmProvider>().provide(), get(), get()) }
    viewModelOf(::JourneyEntryViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule, platformModule)
    }
}
