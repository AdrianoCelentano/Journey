package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.NoteRepository
import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.domain.JourneyNotesService
import com.adriano.journey.domain.Note
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

    override suspend fun loadNotes(): List<Note> {
        return emptyList()
    }
}

class FakeLargeLanguageModel : LargeLanguageModel {
    override suspend fun generateResponse(prompt: String): String = "corrected note"
}

class FakeTextEmbedder : JourneyTextEmbedder {
    override suspend fun embedText(content: String): List<Float> = listOf(1f, 2f, 3f)
}

@OptIn(ExperimentalCoroutinesApi::class)
class JourneyEntryViewModelTest {

    @Test
    fun `test update text intent`() {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        val fakeEmbedder = FakeTextEmbedder()
        val service = JourneyNotesService(fakeLlm, fakeEmbedder, fakeRepo)
        val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)
        viewModel.onIntent(JourneyEntryIntent.UpdateNoteText("Hello World"))

        assertEquals("Hello World", viewModel.state.value.note)
    }

    @Test
    fun `test save note intent successfully saves note and clears text`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeLlm, fakeEmbedder, fakeRepo)
            val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)

            viewModel.onIntent(JourneyEntryIntent.UpdateNoteText("My new note"))
            viewModel.onIntent(JourneyEntryIntent.SaveNote)

            advanceUntilIdle()

            assertEquals("", viewModel.state.value.note)
            assertEquals(1, fakeRepo.savedNotes.size)
            val savedNote = fakeRepo.savedNotes.first()
            assertEquals("corrected note", savedNote.first)
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
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeLlm, fakeEmbedder, fakeRepo)
            val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)

            viewModel.onIntent(JourneyEntryIntent.UpdateNoteText("   "))
            viewModel.onIntent(JourneyEntryIntent.SaveNote)

            advanceUntilIdle()

            assertEquals("   ", viewModel.state.value.note)
            assertEquals(0, fakeRepo.savedNotes.size)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
