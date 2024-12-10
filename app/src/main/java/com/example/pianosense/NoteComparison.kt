package com.example.pianosense

data class NoteComparison(
    val originalNote: String,
    val recordedNote: String,
    val isCorrect: Boolean
)
