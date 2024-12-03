package com.example.pianosense

import MusicViewModel
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    // MusicViewModel kullanımı
    private val musicViewModel: MusicViewModel by activityViewModels()

    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter

    // Müzik listesi
    private val musicList = listOf(
        Music(1, "Valse", "Evgeny Grinko", R.drawable.avatar),
        Music(2, "Moonlight Sonata", "Beethoven", R.drawable.avatar),
        Music(3, "Für Elise", "Beethoven", R.drawable.avatar)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // RecyclerView ve Adapter kurulumu
        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        musicAdapter = MusicAdapter(requireContext(), musicList) { selectedMusic ->
            // Müzik seçildiğinde ViewModel'e bilgiyi ekle ve PlayFragment'e geçiş yap
            musicViewModel.setSelectedMusic(selectedMusic)
            (activity as? MainActivity)?.navigateToPlayFragment()
        }
        musicRecyclerView.adapter = musicAdapter
        musicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
