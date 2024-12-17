package com.example.pianosense

import MusicViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedFragment : Fragment() {

    private lateinit var savedRecyclerView: RecyclerView
    private lateinit var savedAdapter: MusicAdapter
    private val musicList = mutableListOf<Music>()
    private val musicViewModel: MusicViewModel by activityViewModels()

    // Firebase Auth ve Database referansları
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        // RecyclerView ve Adapter kurulumu
        savedRecyclerView = view.findViewById(R.id.savedMusicRecyclerView)
        savedAdapter = MusicAdapter(requireContext(), musicList) { selectedMusic ->
            // Müzik seçildiğinde ViewModel'e bilgiyi ekle ve PlayFragment'e geçiş yap
            musicViewModel.setSelectedMusic(selectedMusic)
            (activity as? MainActivity)?.navigateToPlayFragment()
        }
        savedRecyclerView.adapter = savedAdapter
        savedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSavedMusic() // Firebase'den müzikleri yükle

        return view
    }

    override fun onResume() {
        super.onResume()
        loadSavedMusic() // Her açıldığında verileri güncelle
    }

    private fun loadSavedMusic() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseRef = database.getReference("users").child(userId).child("saved_music")

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    musicList.clear()
                    for (child in snapshot.children) {
                        val music = child.getValue(Music::class.java)
                        if (music != null) {
                            musicList.add(music)
                        }
                    }
                    savedAdapter.notifyDataSetChanged() // RecyclerView güncelle
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to load saved music: ${error.message}", error.toException())
                    Toast.makeText(requireContext(), "Failed to load saved music", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
