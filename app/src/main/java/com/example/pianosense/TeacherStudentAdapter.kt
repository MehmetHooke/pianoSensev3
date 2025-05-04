package com.example.pianosense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeacherStudentAdapter(
    private val studentList: List<User>,
    private val musicList: List<Music>,
    private val onHistoryClick: (studentUid: String, musicList: List<Music>) -> Unit
) : RecyclerView.Adapter<TeacherStudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.textStudentName)
        val emailText: TextView = itemView.findViewById(R.id.textStudentEmail)
        val historyButton: Button = itemView.findViewById(R.id.buttonViewHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.nameText.text = student.name
        holder.emailText.text = student.email

        holder.historyButton.setOnClickListener {
            onHistoryClick(student.uid, musicList)
        }
    }

    override fun getItemCount(): Int = studentList.size
}
