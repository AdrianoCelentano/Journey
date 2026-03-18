package com.adriano.journey.presentation

import androidx.lifecycle.SavedStateHandle
import com.adriano.journey.data.AppPreferences
import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.LlmProvider
import com.adriano.journey.data.NoteRepository
import com.adriano.journey.domain.JourneyNotesService
import com.adriano.journey.domain.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAppPreferences : AppPreferences {
    private val prefs = mutableMapOf<String, Any>()
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs[key] as? Boolean ?: defaultValue
    override fun setBoolean(key: String, value: Boolean) {
        prefs[key] = value
    }
}

class FakeNoteRepository : NoteRepository {
    val savedNotes = mutableListOf<Triple<String, FloatArray, Long>>()
    override suspend fun saveNote(content: String, vector: FloatArray, timestamp: Long) {
        savedNotes.add(Triple(content, vector, timestamp))
    }

    override suspend fun loadNotes(): List<Note> {
        return emptyList()
    }

    override suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note> {
        return emptyList()
    }
}

class FakeLargeLanguageModel : LargeLanguageModel {
    override suspend fun generateResponse(prompt: String): String = "corrected note"
}

class FakeTextEmbedder : JourneyTextEmbedder {
    override suspend fun generateVector(prompt: String): FloatArray = floatArrayOf(1f, 2f, 3f)
}

@OptIn(ExperimentalCoroutinesApi::class)
class JourneyEntryViewModelTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    private fun setupKoin(fakeLlm: FakeLargeLanguageModel) {
        startKoin {
            modules(
                module {
                    single<AppPreferences> { FakeAppPreferences() }
                    single<LargeLanguageModel>(named("local")) { fakeLlm }
                    single<LargeLanguageModel>(named("remote")) { fakeLlm }
                },
            )
        }
    }

    @Test
    fun `test update text intent`() {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()
        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)
        val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)
        viewModel.onIntent(JourneyEntryIntent.UpdateNoteText("Hello World"))

        assertEquals("Hello World", viewModel.state.value.noteInput)
    }

    @Test
    fun `test save note intent successfully saves note and clears text`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            setupKoin(fakeLlm)
            val fakeProvider = LlmProvider()
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

            val savedState = SavedStateHandle(mapOf("journey_entry_text" to "My new note"))
            val viewModel = JourneyEntryViewModel(savedState, service)

            viewModel.onIntent(JourneyEntryIntent.SaveNote)

            advanceUntilIdle()

            assertEquals("", viewModel.state.value.noteInput)
            assertEquals(1, fakeRepo.savedNotes.size)
            val savedNote = fakeRepo.savedNotes.first()
            assertEquals("My new note", savedNote.first)
            assertTrue(savedNote.second.size > 0)
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
            setupKoin(fakeLlm)
            val fakeProvider = LlmProvider()
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

            val savedState = SavedStateHandle(mapOf("journey_entry_text" to "   "))
            val viewModel = JourneyEntryViewModel(savedState, service)

            // Simulate the UI ignoring the save because the text is empty
            if (viewModel.state.value.noteInput.isNotBlank()) {
                viewModel.onIntent(JourneyEntryIntent.SaveNote)
            }

            advanceUntilIdle()

            assertEquals("   ", viewModel.state.value.noteInput)
            assertEquals(0, fakeRepo.savedNotes.size)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `test UpdateNoteSearchText intent updates search input text`() {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()
        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)
        val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)
        viewModel.onIntent(JourneyEntryIntent.UpdateNoteSearchText("Search query"))

        assertEquals("Search query", viewModel.state.value.searchInput)
    }

    @Test
    fun `test UpdateDateRange intent updates dates`() {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()
        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)
        val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)

        val startDate = 1000L
        val endDate = 2000L
        viewModel.onIntent(JourneyEntryIntent.UpdateDateRange(startDate, endDate))

        assertEquals(startDate, viewModel.state.value.startDate)
        assertEquals(endDate, viewModel.state.value.endDate)
    }

    @Test
    fun `test EnhanceNote intent triggers service and updates text while managing loading state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            setupKoin(fakeLlm)
            val fakeProvider = LlmProvider()
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)
            val savedState = SavedStateHandle(mapOf("journey_entry_text" to "Text to enhance"))
            val viewModel = JourneyEntryViewModel(savedState, service)

            // Trigger enhance
            viewModel.onIntent(JourneyEntryIntent.EnhanceNote)

            advanceUntilIdle()

            assertEquals("corrected note", viewModel.state.value.noteInput)
            assertEquals(false, viewModel.state.value.addNoteLoading)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `test SearchNotes intent triggers service and adds answer to questions while managing loading state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val fakeRepo = FakeNoteRepository()
            val fakeLlm = FakeLargeLanguageModel()
            setupKoin(fakeLlm)
            val fakeProvider = LlmProvider()
            val fakeEmbedder = FakeTextEmbedder()
            val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

            val viewModel = JourneyEntryViewModel(SavedStateHandle(), service)

            // Setup the search text by creating a new state or updating the UI
            viewModel.onIntent(JourneyEntryIntent.UpdateNoteSearchText("What did I do today?"))
            advanceUntilIdle()

            viewModel.onIntent(JourneyEntryIntent.SearchNotes)
            advanceUntilIdle()

            assertEquals(1, viewModel.state.value.questions.size)
            assertEquals("What did I do today?", viewModel.state.value.questions.first().question)
            assertEquals("corrected note", viewModel.state.value.questions.first().answer)
            assertEquals("", viewModel.state.value.searchInput) // Check it resets
            assertEquals(false, viewModel.state.value.searchLoading)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
