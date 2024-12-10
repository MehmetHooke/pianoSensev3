package com.example.pianosense

import MusicViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckResultsFragment : Fragment() {

    private val musicViewModel: MusicViewModel by activityViewModels()
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_check_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.resultsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter'i boş bir listeyle başlat
        timelineAdapter = TimelineAdapter(emptyList())
        recyclerView.adapter = timelineAdapter

        // ViewModel'den sonuçları gözlemle
        musicViewModel.analysisResults.observe(viewLifecycleOwner) { results ->
            // Yeni listeyi adapter'a gönder
            timelineAdapter.updateList(results)
        }
    }
}
