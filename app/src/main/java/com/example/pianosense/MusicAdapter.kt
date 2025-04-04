package com.example.pianosense

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
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

            val imageSource = if (music.coverImageUrl.isNotEmpty()) {
                music.coverImageUrl
            } else {
                music.imageResId
            }

            Glide.with(context)
                .load(imageSource)
                .centerCrop()
                .into(imageView)

            // Köşeleri yuvarlatmak için OutlineProvider ekle
            imageView.post {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            // %20 yuvarlatma için view genişliğinin %20'si kadar yarıçap hesaplanıyor.
                            val radius = (view.width * 0.2f)
                            outline.setRoundRect(0, 0, view.width, view.height, radius)
                        }
                    }
                    imageView.clipToOutline = true
                }
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
