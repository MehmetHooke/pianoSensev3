package com.example.pianosense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class OgrenciHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryItem>()

    private lateinit var studentUid: String
    private lateinit var staticMusicList: List<Music>  // Serializable ile gelecek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentUid = arguments?.getString("student_uid") ?: ""
        // Serializable olarak gelen staticMusicList
        @Suppress("UNCHECKED_CAST")
        staticMusicList = arguments?.getSerializable("music_list") as? ArrayList<Music> ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ogrenci_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewOgrenciHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(historyList, staticMusicList)
        recyclerView.adapter = adapter

        loadStudentHistory()
    }

    private fun loadStudentHistory() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(studentUid)
            .child("result_history")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(HistoryItem::class.java)
                    if (item != null) {
                        historyList.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Gerekirse log veya Toast ile hata bildirimi
            }
        })
    }
}
