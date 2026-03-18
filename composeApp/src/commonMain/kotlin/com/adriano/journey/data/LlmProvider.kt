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

    private val localLlm: LargeLanguageModel by inject(named("local"))

    private val remoteLlm: LargeLanguageModel by inject(named("remote"))

    fun provide(): LargeLanguageModel {
        return if (isLocalModelEnabled) localLlm else remoteLlm
    }
}
