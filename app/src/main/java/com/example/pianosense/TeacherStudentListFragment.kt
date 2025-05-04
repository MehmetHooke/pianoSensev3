package com.example.pianosense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TeacherStudentListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherStudentAdapter
    private val studentList = mutableListOf<User>()
    private val musicList = mutableListOf<Music>()

    private lateinit var classCode: String
    private lateinit var currentTeacherUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTeacherUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        classCode = arguments?.getString("class_code") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_student_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerViewTeacherStudents)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TeacherStudentAdapter(studentList, musicList) { studentUid, musicList ->
            openOgrenciHistoryFragment(studentUid, musicList)
        }

        recyclerView.adapter = adapter

        loadMusicList()
        loadStudentList()
    }

    private fun loadMusicList() {
        val ref = FirebaseDatabase.getInstance().getReference("music_list")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                musicList.clear()
                for (child in snapshot.children) {
                    val music = child.getValue(Music::class.java)
                    if (music != null) musicList.add(music)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadStudentList() {
        val studentsRef = FirebaseDatabase.getInstance()
            .getReference("classes")
            .child(classCode)
            .child("students")

        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()

                val totalStudents = snapshot.childrenCount
                var loadedCount = 0

                for (child in snapshot.children) {
                    val uid = child.key ?: continue
                    val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                            val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                            val user = User(uid, name, email)
                            studentList.add(user)

                            loadedCount++
                            if (loadedCount == totalStudents.toInt()) {
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun loadUserData(uid: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val user = User(uid, name, email)
                studentList.add(user)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openOgrenciHistoryFragment(studentUid: String, musicList: List<Music>) {
        val fragment = OgrenciHistoryFragment()
        val bundle = Bundle()
        bundle.putString("student_uid", studentUid)
        bundle.putSerializable("music_list", ArrayList(musicList)) // Music sınıfı Serializable
        fragment.arguments = bundle

        parentFragmentManager.commit {
            replace(R.id.teacherPanelContainer, fragment)
            addToBackStack(null)
        }

    }
}
