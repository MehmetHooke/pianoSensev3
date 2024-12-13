package com.example.pianosense

import MusicViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckResultsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check_results, container, false)
        recyclerView = view.findViewById(R.id.resultsRecyclerView)

        setupRecyclerView()
        observeAnalysisResults()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TimelineAdapter(emptyList()) // Başlangıçta boş bir listeyle başlatıyoruz.
        recyclerView.adapter = adapter
    }

    private fun observeAnalysisResults() {
        viewModel.analysisResults.observe(viewLifecycleOwner) { comparisonResults ->
            adapter.updateData(comparisonResults) // Adapter'deki verileri güncelliyoruz.
        }
    }
}
