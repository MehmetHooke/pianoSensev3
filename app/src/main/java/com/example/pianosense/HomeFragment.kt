package com.example.pianosense

import MusicViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    val musicList = listOf(
        Music(1, "Kayıt 1", "Beethoven", R.drawable.bethoven, "originalMusic1.wav"),
        Music(2, "Kayıt 2 ", "Mozart", R.drawable.mozart, "originalMusic2.wav"),
        Music(3, "9. Senfoni", "Bethoven", R.drawable.bethoven, "originalMusic1.wav"),
        Music(4, "Divenire", "Ludovico Einaudi", R.drawable.ludovico_einaudi, "originalMusic1.wav"),
        Music(5, "Hit the Road Jack", "Ray Charles", R.drawable.ray_charles, "originalMusic1.wav"),
        Music(6, "Hold the Line", "Toto", R.drawable.toto, "originalMusic1.wav"),
        Music(7, "Someone Like You", "Adele", R.drawable.adele, "originalMusic1.wav"),
        Music(8, "Comptine d’un autre été l’après", "Yann Tiersen", R.drawable.yann_tiersen, "originalMusic1.wav"),
        Music(9, "Parisienne Moonlight", "Anathema", R.drawable.anathema, "originalMusic1.wav"),
        Music(10, "İmagine", "John Lennon", R.drawable.john_lennon, "originalMusic1.wav"),
        Music(11, "Brother John", "Anonim", R.drawable.brother_john, "originalMusic1.wav"),
        Music(12, "Für Elise", "Beethoven", R.drawable.bethoven, "originalMusic1.wav"),
        Music(13, "Valse", "Evegny Grinko", R.drawable.evegny_grinko, "originalMusic1.wav")


    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }

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

