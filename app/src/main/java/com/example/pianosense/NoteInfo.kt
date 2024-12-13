package com.example.pianosense

data class NoteInfo(
    val originalNote: String? = null, // Nullable String
    val recordedNote: String? = null, // Nullable String
    val timestamp: Double,
    val isCorrect: Boolean
)
