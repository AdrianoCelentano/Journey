package com.adriano.journey.data.db.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class NoteEntity(
    @Id
    var id: Long = 0,
    var content: String,
    var contentVector: FloatArray,
    var timestamp: Long,
)
