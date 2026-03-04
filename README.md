# 🧠 Journey: AI-Powered Second Brain

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![AI](https://img.shields.io/badge/AI%20%2F%20RAG-Offline_First-blue?style=for-the-badge)

**Journey** is a privacy-first, AI-powered note-taking Android application. It acts as a conceptual second brain, allowing users to converse with their past thoughts using Retrieval-Augmented Generation (RAG) and vector embeddings.

## 🚀 Features

* **Semantic Search via RAG:** Instead of standard keyword matching, notes are stored as mathematical vector embeddings. When searching, the app converts your query into a concept, retrieves the top 5 most relevant notes, and uses an LLM to formulate a precise answer based *only* on your historical data.
* **Offline-First & Privacy Focused:** Users can toggle between a fully local AI model (running on-device) for maximum privacy, or connect to a cloud-based LLM for more complex reasoning. This switch happens dynamically at runtime—no app restart required.
* **Smart Enhancement:** A drafting assistant that gently polishes grammar and structure without altering the original meaning or hallucinating new facts.
* **Time-Traveling Ideas:** Filter your conceptual searches by precise date ranges to see how your thoughts on a specific topic have evolved over time. (Powered by standard UI date pickers for reliable precision before hitting the vector database).

## 🏗 Architecture & Domain Logic

Journey implements a clean, decoupled architecture focusing on state management and efficient database querying.

### Note Creation Flow
1. **Drafting:** User writes a note.
2. **Enhancing (Optional):** AI polishes the text.
3. **Saving & Vectorizing:** The note is saved to the local database, and a vector embedding (mathematical representation) is generated and stored alongside it for semantic understanding.

### Semantic Search Flow
1. **Analyze:** User asks a question; the app converts it into a vector concept.
2. **Retrieve:** The local vector database is queried to find the top 5 most conceptually similar notes (bounded by a user-defined date range).
3. **Generate:** The LLM reads the retrieved notes and formulates a direct answer based purely on that context.

## 🛠 Tech Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose Multiplatform
* **Architecture:** MVVM / Clean Architecture
* **AI/Local Models:** MediaPipe (Gemma) & ML Kit GenAI (Gemini Nano)
* **Database:** Room + SQLite Vector

## 🤝 Let's Connect
I built Journey to solve my own problem of organizing and navigating my thoughts. I am currently looking for my next role in Android & AI Development. 

[Connect with me on LinkedIn](https://www.linkedin.com/in/adrianschaefer/)
