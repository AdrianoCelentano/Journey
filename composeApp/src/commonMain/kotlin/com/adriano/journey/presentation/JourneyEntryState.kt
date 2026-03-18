package com.adriano.journey.presentation

data class JourneyEntryState(
    val noteInput: String = "",
    val searchInput: String = "",
    val questions: List<Question> = emptyList(),
    val startDate: Long? = null,
    val endDate: Long? = null,
    val searchLoading: Boolean = false,
    val addNoteLoading: Boolean = false,
    val enhanceNoteLoading: Boolean = false,
) {
    val enableEnhanceButton: Boolean
        get() = noteInput.isNotBlank() && !enhanceNoteLoading

    val enableSaveButton: Boolean
        get() = noteInput.isNotBlank() && !addNoteLoading && !enhanceNoteLoading

    val enableSearchButton: Boolean
        get() = searchInput.isNotBlank() && !searchLoading

    val enableClearButton: Boolean
        get() = questions.isNotEmpty()

    val enableDateRangeButton: Boolean
        get() = !searchLoading

    val enableNoteInput: Boolean
        get() = !enhanceNoteLoading

    val enableSearchTextField: Boolean
        get() = !searchLoading
}

data class Question(
    val question: String,
    val answer: String,
)
