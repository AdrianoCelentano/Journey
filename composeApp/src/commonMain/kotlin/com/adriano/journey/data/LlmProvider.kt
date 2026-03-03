package com.adriano.journey.data

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class LlmProvider : KoinComponent {

    var isLocalModelEnabled: Boolean = true

    private val localLlm: LargeLanguageModel by inject(named("local"))

    private val remoteLlm: LargeLanguageModel by inject(named("remote"))

    fun provide(): LargeLanguageModel {
        return if (isLocalModelEnabled) localLlm else remoteLlm
    }
}
