package com.example.pianosense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter(private var data: List<ComparisonResult>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val originalNoteTextView: TextView = view.findViewById(R.id.originalNoteTextView)
        val recordedNoteTextView: TextView = view.findViewById(R.id.recordedNoteTextView)
        val isCorrectTextView: TextView = view.findViewById(R.id.isCorrectTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.originalNoteTextView.text = item.originalNote.toString() // NoteInfo'yu String'e çevirir
        holder.recordedNoteTextView.text = item.recordedNote.toString()
        holder.isCorrectTextView.text = if (item.isCorrect) "Correct" else "Wrong"
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<ComparisonResult>) {
        data = newData
        notifyDataSetChanged()
    }
}

