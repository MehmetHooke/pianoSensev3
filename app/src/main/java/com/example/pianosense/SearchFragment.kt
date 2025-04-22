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
    private var musicList: List<Music> = emptyList()
    private var filteredList: MutableList<Music> = mutableListOf()

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

        // RecyclerView ve Adapter kurulumu
        musicAdapter = MusicAdapter(requireContext(), filteredList) { selectedMusic ->
            // Seçilen müzik bilgisini ViewModel'e aktarıp PlayFragment'e geçiş yapıyoruz.
            musicViewModel.setSelectedMusic(selectedMusic)
            (activity as? MainActivity)?.navigateToPlayFragment()
        }
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = musicAdapter

        // Firebase'den dinamik müzik listesini gözlemleyin
        musicViewModel.musicList.observe(viewLifecycleOwner) { fetchedMusicList ->
            musicList = fetchedMusicList
            // Arama kutusu boş ise yalnızca ilk 4 müzik gösterilsin
            filteredList.clear()
            if (musicList.size > 4) {
                filteredList.addAll(musicList.subList(0, 4))
            } else {
                filteredList.addAll(musicList)
            }
            musicAdapter.notifyDataSetChanged()
        }

        // searchEditText odaklandığında önerilen müzikleri gizle/göster
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.GONE
            } else if (!hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.VISIBLE
            }
        }

        // Arama çubuğuna TextWatcher ekleyin
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMusicList(s.toString())
                suggestedMusics.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Arama sonuçlarını filtreleme fonksiyonu: Hem boş arama hem de arama sonuçları 4 ile sınırlandırılıyor.
    private fun filterMusicList(query: String) {
        val filtered = if (query.isEmpty()) {
            // Arama kutusu boşsa ilk 4 öğeyi al
            if (musicList.size > 4) musicList.subList(0, 4) else musicList
        } else {
            val lowercaseQuery = query.lowercase()
            musicList.filter {
                it.title.lowercase().contains(lowercaseQuery) ||
                        it.composer.lowercase().contains(lowercaseQuery)
            }
        }
        filteredList.clear()
        if (filtered.size > 4) {
            filteredList.addAll(filtered.subList(0, 4))
        } else {
            filteredList.addAll(filtered)
        }
        musicAdapter.notifyDataSetChanged()
    }
}
