package com.adriano.journey.data.db.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class NoteEntity(
    @Id
    val id: Long,
    val content: String,
    val timestamp: Long,
)