# String to Vector using MediaPipe

## Overview (Domain-Specific)
The Journey application utilizes an on-device Large Language Model (LLM) to power feature-rich interactions, such as searching through saved entries based on their meaning.

To effectively search through text using artificial intelligence, the platform uses a mathematical representation of words called **Vectors** (or Embeddings). In the past, these vectors were temporarily saved as long strings of text. This enhancement upgrades the application to correctly process and save these mathematical vectors in their true form (FloatArrays), ensuring that notes are correctly prepared for fast, meaningful searches.

When a note is created, it goes through an embedding processor which translates the written text into an array of decimal numbers. This array fundamentally captures the "meaning" of the user's note.

## Technical Details

The issue required replacing the `String` placeholder for vector data with actual float arrays generated explicitly using `com.google.mediapipe:tasks-text`'s `TextEmbedder`.

### MediaPipe Integration
We integrated the MediaPipe `TextEmbedder` instance inside `LargeLanguageModelMediaPipe.kt` using a specialized inference model.
1. The model used is `embeddinggemma-300M_seq512_mixed-precision.tflite`.
2. The options are instantiated using `BaseOptions.builder()` and passed to `TextEmbedderOptions.builder()`.
3. An `embed(prompt)` invocation is performed asynchronously which returns an `EmbeddingResult`.
4. The float arrays are extracted using `result.embeddingResult().embeddings().get(0).floatEmbedding().toList()`.

### Room Database Updates
The core data structure in Room requires `FloatArray` representation.
- `NoteEntity` was migrated to define `contentVector: FloatArray`.
- A TypeConverter `FloatArrayConverter` was implemented to transparently serialize/deserialize `FloatArray` to JSON whenever read from or written to the respective SQLite tables.
- `NoteRepositoryImpl` was updated to drop manual JSON conversions in favor of the implicit Room conversion using the new entity property type.

### Architecture Flow

```mermaid
sequenceDiagram
    participant User
    participant Repository as NoteRepository
    participant Embedding as TextEmbedder
    participant DB as Room/AppDatabase

    User->>Repository: saveNote("The quick brown fox", vector, timestamp)
    NoteRepository->>Embedding: generateVector("The quick brown fox")
    Embedding-->>Repository: returns List<Float>
    Repository->>DB: save NoteEntity with contentVector as FloatArray
    Note Note over DB: FloatArrayConverter converts FloatArray <-> JSON internally
    DB-->>User: Note saved successfully
```
