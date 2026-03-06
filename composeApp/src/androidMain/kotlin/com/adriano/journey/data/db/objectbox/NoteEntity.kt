package com.adriano.journey.data.db.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id

@Entity
data class NoteEntity(
    @Id
    var id: Long = 0,
    var content: String,
    @HnswIndex(dimensions = 512)
    var contentVector: FloatArray,
    var timestamp: Long,
)
