package com.example.pianosense

import java.io.Serializable

data class Music(
    val id: Int = -1, // Varsayılan değerler ekleniyor
    val title: String = "",
    val composer: String = "",
    val imageResId: Int = 0,
    val audioFilePath: String = "" // Yeni özellik için varsayılan değer
) : Serializable