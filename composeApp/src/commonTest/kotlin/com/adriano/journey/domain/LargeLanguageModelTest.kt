package com.adriano.journey.domain

import kotlin.test.Test
import kotlin.test.assertTrue

class LargeLanguageModelTest {

    @Test
    fun testMockImplementationInstantiates() {
        val llm = object : LargeLanguageModel {
            override suspend fun generateResponse(prompt: String): String {
                return "Mocked Response: $prompt"
            }
        }

        assertTrue(llm != null)
    }
}
