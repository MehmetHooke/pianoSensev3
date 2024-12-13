package com.example.pianosense

data class ComparisonResult(
    val originalNote: NoteInfo?,
    val recordedNote: NoteInfo?,
    val isCorrect: Boolean
)


