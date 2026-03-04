package com.adriano.journey.domain

import com.adriano.journey.data.JourneyTextEmbedder
import com.adriano.journey.data.LargeLanguageModel
import com.adriano.journey.data.LlmProvider
import com.adriano.journey.data.NoteRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeNoteRepository : NoteRepository {
    val savedNotes = mutableListOf<Triple<String, List<Float>, Long>>()
    var notesToReturn = emptyList<Note>()

    override suspend fun saveNote(content: String, vector: List<Float>, timestamp: Long) {
        savedNotes.add(Triple(content, vector, timestamp))
    }

    override suspend fun loadNotes(): List<Note> = notesToReturn
}

class FakeLargeLanguageModel : LargeLanguageModel {
    var generateResponseResult = "generated response"
    var lastPrompt = ""

    override suspend fun generateResponse(prompt: String): String {
        lastPrompt = prompt
        return generateResponseResult
    }
}

class FakeLlmProvider(private val model: LargeLanguageModel) : LlmProvider() {
    override var isLocalModelEnabled: Boolean = true
    override fun provide(): LargeLanguageModel = model
}

class FakeTextEmbedder : JourneyTextEmbedder {
    var vectorToReturn = listOf(1f, 2f, 3f)
    override suspend fun generateVector(prompt: String): List<Float> = vectorToReturn
}

class JourneyNotesServiceTest {

    @Test
    fun `test addEntry generates vector and saves to repository`() = runTest {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        val fakeProvider = FakeLlmProvider(fakeLlm)
        val fakeEmbedder = FakeTextEmbedder()
        fakeEmbedder.vectorToReturn = listOf(0.1f, 0.2f, 0.3f)

        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

        service.addEntry("A test note")

        assertEquals(1, fakeRepo.savedNotes.size)
        val savedNote = fakeRepo.savedNotes.first()
        assertEquals("A test note", savedNote.first)
        assertEquals(listOf(0.1f, 0.2f, 0.3f), savedNote.second)
    }

    @Test
    fun `test enhanceNote uses LLM to generate response from prompt`() = runTest {
        val fakeRepo = FakeNoteRepository()
        val fakeLlm = FakeLargeLanguageModel()
        fakeLlm.generateResponseResult = "An enhanced test note."
        val fakeProvider = FakeLlmProvider(fakeLlm)
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
        val note1 = Note(1, "Similar Note content", listOf(1.0f, 0.0f, 0.0f), 0L)
        // Note 2: completely different vector
        val note2 = Note(2, "Different Note content", listOf(0.0f, 1.0f, 0.0f), 0L)

        fakeRepo.notesToReturn = listOf(note1, note2)

        val fakeLlm = FakeLargeLanguageModel()
        fakeLlm.generateResponseResult = "Answer from similar note"
        val fakeProvider = FakeLlmProvider(fakeLlm)
        val fakeEmbedder = FakeTextEmbedder()
        // Vector pointing in the same direction as note1
        fakeEmbedder.vectorToReturn = listOf(1.0f, 0.0f, 0.0f)

        val service = JourneyNotesService(fakeProvider, fakeEmbedder, fakeRepo)

        val result = service.searchEntries("search query")

        assertEquals("Answer from similar note", result)
        assertTrue(fakeLlm.lastPrompt.contains("Similar Note content"))
        assertTrue(fakeLlm.lastPrompt.contains("search query"))
    }
}
