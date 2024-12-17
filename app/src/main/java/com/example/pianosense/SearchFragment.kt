package com.example.pianosense

import MusicViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    // ViewModel kullanımı
    private val musicViewModel: MusicViewModel by activityViewModels()

    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicList: List<Music>
    private lateinit var filteredList: MutableList<Music>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val suggestedMusics = view.findViewById<TextView>(R.id.suggestedMusics)

        // Müzik listesi
        musicList = listOf(
            Music(1, "Moonlight Sonata", "Beethoven", R.drawable.bethoven, "originalMusic1.wav"),
            Music(2, "Türk Marşı ", "Mozart", R.drawable.mozart, "mozartturkishmarch.wav"),
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
        filteredList = musicList.toMutableList()

        // RecyclerView ve Adapter kurulumu
        musicAdapter = MusicAdapter(requireContext(), filteredList) { selectedMusic ->
            // ViewModel'e seçilen müzik bilgisini ekliyoruz
            musicViewModel.setSelectedMusic(selectedMusic)
            // PlayFragment'e geçiş yapıyoruz
            (activity as? MainActivity)?.navigateToPlayFragment()
        }
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = musicAdapter

        // searchEditText'e odaklandığında önerilen müzikleri gizlemek için listener ekliyoruz
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.GONE
            } else if (!hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.VISIBLE
            }
        }

        // Arama çubuğuna TextWatcher ekliyoruz
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMusicList(s.toString())
                suggestedMusics.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Arama sonuçlarını filtreleme fonksiyonu
    private fun filterMusicList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(musicList)
        } else {
            val lowercaseQuery = query.lowercase()
            filteredList.addAll(musicList.filter {
                it.title.lowercase().contains(lowercaseQuery) ||
                        it.composer.lowercase().contains(lowercaseQuery)
            })
        }
        musicAdapter.notifyDataSetChanged()
    }
}
