package com.example.pianosense

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MusicAdapter(
    private val context: Context,
    private var musicList: List<Music>,
    private val onItemClick: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.musicTitle)
        private val composerTextView: TextView = itemView.findViewById(R.id.musicComposer)
        private val imageView: ImageView = itemView.findViewById(R.id.musicImage)

        fun bind(music: Music) {
            titleTextView.text = music.title
            composerTextView.text = music.composer

            // Eğer dinamik URL varsa Glide ile yükle, yoksa yerel drawable'ı kullan
            if (music.coverImageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(music.coverImageUrl)
                    .into(imageView)
            } else {
                imageView.setImageResource(music.imageResId)
            }

            itemView.setOnClickListener {
                onItemClick(music)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(musicList[position])
    }

    override fun getItemCount(): Int = musicList.size

    // Yeni veri geldiğinde adapterin listesini güncellemek için updateList metodu
    fun updateList(newList: List<Music>) {
        musicList = newList
        notifyDataSetChanged()
    }
}
