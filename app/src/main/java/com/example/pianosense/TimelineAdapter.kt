package com.example.pianosense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter(private var notes: List<NoteInfo>) :
    RecyclerView.Adapter<TimelineAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val timestampTextView : TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteInfo = notes[position]
        val formattedTimestamp = formatTimestamp(noteInfo.timestamp)
        holder.noteTextView.text = "${noteInfo.recordedNote}"
        holder.timestampTextView.text = "Time: $formattedTimestamp"

        if (noteInfo.isCorrect) {
            holder.noteTextView.setBackgroundResource(R.color.green)
        } else {
            holder.noteTextView.setBackgroundResource(R.color.red)
        }
    }


    override fun getItemCount(): Int {
        return notes.size
    }

    fun formatTimestamp(timestamp: Double): String {
        val seconds = timestamp
        return String.format("%.2f s", seconds)
    }


    // Listeyi güncellemek için bir yöntem ekleyin
    fun updateList(newNotes: List<NoteInfo>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
