package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeNoteRepository : NoteRepository {
    val savedNotes = mutableListOf<Triple<String, List<Float>, Long>>()
    override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {
        savedNotes.add(Triple(content, vector, timestamp))
    }
}

class FakeLargeLanguageModel : LargeLanguageModel {
    override suspend fun generateResponse(prompt: String): String = "response"
    override suspend fun generateVector(prompt: String): List<Float> = listOf(1f, 2f, 3f)
}

@OptIn(ExperimentalCoroutinesApi::class)
class JourneyEntryViewModelTest {

    @Test
    fun `test update text intent`() {
        val viewModel = JourneyEntryViewModel(SavedStateHandle(), FakeNoteRepository(), FakeLargeLanguageModel())
        viewModel.onIntent(JourneyEntryIntent.UpdateText("Hello World"))

        assertEquals("Hello World", viewModel.state.value.text)
    }

    @Test
    fun `test save note intent successfully saves note and clears text`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            val viewModel = JourneyEntryViewModel(SavedStateHandle(), fakeRepo, fakeLlm)

            viewModel.onIntent(JourneyEntryIntent.UpdateText("My new note"))
            viewModel.onIntent(JourneyEntryIntent.SaveNote)

            advanceUntilIdle()

            assertEquals("", viewModel.state.value.text)
            assertEquals(1, fakeRepo.savedNotes.size)
            val savedNote = fakeRepo.savedNotes.first()
            assertEquals("My new note", savedNote.first)
            assertTrue(savedNote.second.isNotEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `test save note intent ignores empty text`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            val viewModel = JourneyEntryViewModel(SavedStateHandle(), fakeRepo, fakeLlm)

            viewModel.onIntent(JourneyEntryIntent.UpdateText("   "))
            viewModel.onIntent(JourneyEntryIntent.SaveNote)

            advanceUntilIdle()

            assertEquals("   ", viewModel.state.value.text)
            assertEquals(0, fakeRepo.savedNotes.size)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
