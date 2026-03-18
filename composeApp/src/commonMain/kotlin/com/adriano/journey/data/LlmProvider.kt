package com.adriano.journey.data

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class LlmProvider : KoinComponent {

    private val preferences: AppPreferences by inject()

    var isLocalModelEnabled: Boolean
        get() = preferences.getBoolean("is_local_model_enabled", true)
        set(value) {
            preferences.setBoolean("is_local_model_enabled", value)
        }

    private val localNanoLlm: LargeLanguageModel by inject(named("local_nano"))
    private val localFallbackLlm: LargeLanguageModel by inject(named("local_fallback"))

    private val remoteLlm: LargeLanguageModel by inject(named("remote"))

    suspend fun provide(): LargeLanguageModel {
        if (!isLocalModelEnabled) return remoteLlm
        return if (localNanoLlm.isSupported()) localNanoLlm else localFallbackLlm
    }
}
