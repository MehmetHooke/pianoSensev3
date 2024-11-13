package com.example.pianosense

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private val musicList = listOf(
        Music(1, "Valse", "Evgeny Grinko", R.drawable.avatar),
        Music(2, "Moonlight Sonata", "Beethoven", R.drawable.ic_arrow_right),
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

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        musicAdapter = MusicAdapter(requireContext(), musicList) { selectedMusic ->
            openPlayPage(selectedMusic)
        }
        musicRecyclerView.adapter = musicAdapter
        musicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun openPlayPage(music: Music) {
        Log.d("HomeFragment", "Opening Play Page for music: ${music.title}, composer: ${music.composer}")
        (activity as? MainActivity)?.navigateToPlayFragment(
            music.id,         // Int
            music.title,      // String
            music.composer    // String
        )
    }



}
