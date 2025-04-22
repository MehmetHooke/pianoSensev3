package com.example.pianosense

import java.io.Serializable

data class Music(
    val id: Int = -1,
    val title: String = "",
    val composer: String = "",
    val imageResId: Int = 0,            // Eski sistem: Yerel drawable kaynağı
    val audioFilePath: String = "",     // Eski sistem: Yerel .wav dosya ismi
    val coverImageUrl: String = "",     // Yeni sistem: Firebase Storage’daki görselin URL’si
    val audioFileUrl: String = "" ,      // Yeni sistem: Firebase Storage’daki .wav dosyasının URL’si
    val metronom: String = "4/4",     // yenii bu zaten eklenmişti
    val bpm: Int = 60                // yeni eklenen alan
) : Serializable
