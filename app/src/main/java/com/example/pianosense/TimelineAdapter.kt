package com.example.pianosense

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class TimelineAdapter(private var data: List<ComparisonResult>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val originalNoteTextView: TextView = view.findViewById(R.id.originalNoteTextView)
        val recordedNoteTextView: TextView = view.findViewById(R.id.recordedNoteTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)
        val isCorrectTextView: TextView = view.findViewById(R.id.isCorrectTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = data[position]

        holder.originalNoteTextView.text = "Original Note: " + (result.originalNote?.originalNote ?: "None")
        holder.recordedNoteTextView.text = "Recorded Note: " + (result.recordedNote?.recordedNote ?: "None")
        holder.timestampTextView.text = "Time: " + String.format("%.2f", result.originalNote?.timestamp ?: 0.0)
        holder.isCorrectTextView.text = "Status: " + if (result.isCorrect) "Correct" else "Wrong"
        holder.isCorrectTextView.setTextColor(
            if (result.isCorrect) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        )
    }


    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<ComparisonResult>) {
        data = newData
        notifyDataSetChanged()
    }
}
