package com.adriano.journey.domain

import com.adriano.journey.data.AppPreferences
import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.LlmProvider
import com.adriano.journey.data.NoteRepository
import kotlinx.coroutines.test.runTest
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
    var notesToReturn = emptyList<Note>()

    override suspend fun saveNote(content: String, vector: FloatArray, timestamp: Long) {
        savedNotes.add(Triple(content, vector, timestamp))
    }

    override suspend fun loadNotes(): List<Note> = notesToReturn

    override suspend fun loadMatchingNotes(queryVector: FloatArray): List<Note> = notesToReturn
}

class FakeLargeLanguageModel : LargeLanguageModel {
    var generateResponseResult = "generated response"
    var lastPrompt = ""

    override suspend fun generateResponse(prompt: String): String {
        lastPrompt = prompt
        return generateResponseResult
    }
}

class FakeTextEmbedder : JourneyTextEmbedder {
    var vectorToReturn = floatArrayOf(1f, 2f, 3f)
    override suspend fun generateVector(prompt: String): FloatArray = vectorToReturn
}

class JourneyNotesServiceTest {

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
    fun `test addEntry generates vector and saves to repository`() = runTest {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()
        fakeEmbedder.vectorToReturn = floatArrayOf(0.1f, 0.2f, 0.3f)

        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

        service.addEntry("A test note")

        assertEquals(1, fakeRepo.savedNotes.size)
        val savedNote = fakeRepo.savedNotes.first()
        assertEquals("A test note", savedNote.first)
        assertTrue(floatArrayOf(0.1f, 0.2f, 0.3f).contentEquals(savedNote.second))
    }

    @Test
    fun `test enhanceNote uses LLM to generate response from prompt`() = runTest {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        fakeLlm.generateResponseResult = "An enhanced test note."
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()

        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

        val result = service.enhanceNote("a test note")

        assertEquals("An enhanced test note.", result)
        assertTrue(fakeLlm.lastPrompt.contains("a test note"))
        assertTrue(fakeLlm.lastPrompt.contains("You are a strict text editor."))
    }

    @Test
    fun `test searchEntries finds match using cosine similarity and returns LLM formulated answer`() = runTest {
        val fakeRepo = FakeNoteRepository()
        // Note 1: similar vector to search vector
        val note1 = Note(1, "Similar Note content", floatArrayOf(1.0f, 0.0f, 0.0f), 0L)
        // Note 2: completely different vector
        val note2 = Note(2, "Different Note content", floatArrayOf(0.0f, 1.0f, 0.0f), 0L)

        fakeRepo.notesToReturn = listOf(note1, note2)

        val fakeLlm = FakeLargeLanguageModel()
        fakeLlm.generateResponseResult = "Answer from similar note"
        setupKoin(fakeLlm)
        val fakeProvider = LlmProvider()
        val fakeEmbedder = FakeTextEmbedder()
        // Vector pointing in the same direction as note1
        fakeEmbedder.vectorToReturn = floatArrayOf(1.0f, 0.0f, 0.0f)

        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

        val result = service.searchEntries("search query")

        assertEquals("Answer from similar note", result)
        assertTrue(fakeLlm.lastPrompt.contains("Similar Note content"))
        assertTrue(fakeLlm.lastPrompt.contains("search query"))
    }
}
