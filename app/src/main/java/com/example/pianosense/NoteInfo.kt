package com.example.pianosense;

data class NoteInfo(
    val originalNote: String,
    val recordedNote: String,
    val timestamp: Double,
    val isCorrect: Boolean
)
