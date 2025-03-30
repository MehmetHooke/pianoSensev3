package com.example.pianosense

data class HistoryItem(
    val music_name: String = "",
    val music_image_url: String = "",
    val date_time: String = "",
    val correct_notes: Int = 0,
    val wrong_notes: Int = 0,
    val wrong_note_list: List<String> = emptyList()
)
