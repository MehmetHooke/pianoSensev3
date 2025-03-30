package com.example.pianosense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryAdapter(private val historyList: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // Açılmış (expanded) pozisyonları takip etmek için
    private val expandedPositions = mutableSetOf<Int>()

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicNameTextView: TextView = itemView.findViewById(R.id.historyMusicName)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.historyDateTime)
        val musicImageView: ImageView = itemView.findViewById(R.id.historyMusicImage)
        val detailsLayout: View = itemView.findViewById(R.id.detailsLayout)
        val correctNotesTextView: TextView = itemView.findViewById(R.id.correctNotesTextView)
        val wrongNotesTextView: TextView = itemView.findViewById(R.id.wrongNotesTextView)
        val wrongNoteListTextView: TextView = itemView.findViewById(R.id.wrongNoteListTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.musicNameTextView.text = item.music_name
        holder.dateTimeTextView.text = item.date_time
        if (item.music_image_url.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.music_image_url)
                .into(holder.musicImageView)
        } else {
            holder.musicImageView.setImageResource(R.drawable.avatar)
        }
        holder.correctNotesTextView.text = "Doğru: ${item.correct_notes}"
        holder.wrongNotesTextView.text = "Yanlış: ${item.wrong_notes}"
        holder.wrongNoteListTextView.text = "Yanlış Notalar:\n" +
                if (item.wrong_note_list.isNotEmpty()) item.wrong_note_list.joinToString("\n") else "-"


        // Açık/kapalı durumuna göre detay layout görünürlüğünü ayarla
        holder.detailsLayout.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        // Tıklama ile detay bölümünü aç/kapa
        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = historyList.size
}
