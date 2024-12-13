package com.example.pianosense

data class RecordedNoteInfo(
    val note: String,       // Algılanan kaydedilmiş nota
    val timestamp: Double   // Zaman damgası
)
{
    override fun toString(): String {
        return "RecordedNote: $note, Timestamp: $timestamp"
    }
}
