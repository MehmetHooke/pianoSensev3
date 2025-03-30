package com.example.pianosense

import MusicViewModel
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class CheckResultsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter
    private lateinit var accuracyTextView: TextView
    private lateinit var motivationBox: View
    private lateinit var motivationMessage: TextView
    private lateinit var closeMotivationButton: View
    private lateinit var motivationAnimation: LottieAnimationView
    private var mediaPlayer: MediaPlayer? = null
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check_results, container, false)

        recyclerView = view.findViewById(R.id.resultsRecyclerView)
        accuracyTextView = view.findViewById(R.id.accuracyTextView)
        motivationBox = view.findViewById(R.id.motivationBox)
        motivationMessage = view.findViewById(R.id.motivationMessage)
        closeMotivationButton = view.findViewById(R.id.closeMotivationButton)
        motivationAnimation = view.findViewById(R.id.motivationAnimation)

        setupRecyclerView()
        observeAnalysisResults()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Geri tuşu engelleme
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (motivationBox.visibility == View.VISIBLE) {
                // Popup açıkken geri tuşu hiçbir şey yapmasın
                // İstiyorsan burada Toast bile gösterebilirsin
            } else {
                // Popup kapalıysa normal geri çalışsın (istersen kaldırabilirsin)
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TimelineAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun observeAnalysisResults() {
        viewModel.analysisResults.observe(viewLifecycleOwner) { comparisonResults ->
            adapter.updateData(comparisonResults)

            val totalNotes = comparisonResults.size
            val correctNotes = comparisonResults.count { it.isCorrect }
            val accuracy = if (totalNotes > 0) (correctNotes * 100) / totalNotes else 0

            accuracyTextView.text =
                "Correct Note:$correctNotes/Total Note:$totalNotes (Accuracy: %$accuracy)"

            val message: String
            val soundFile: String

            when {
                accuracy >= 80 -> {
                    message = "Tebrikler! Harika çaldın 🎉"
                    soundFile = "IyiSes.mp3"
                    motivationAnimation.visibility = View.VISIBLE
                    motivationAnimation.setAnimation("fireworks.json")
                    motivationAnimation.playAnimation()
                }
                accuracy >= 50 -> {
                    message = "İyi gidiyorsun, daha iyisini yapabilirsin 💪"
                    soundFile = "ortaSes.mp3"
                    motivationAnimation.visibility = View.VISIBLE
                    motivationAnimation.setAnimation("fireworks.json")
                    motivationAnimation.playAnimation()
                }
                else -> {
                    message = "Hadi tekrar deneyelim! Eminim daha iyi yapabilirsin 🌟"
                    soundFile = "kotuSes.mp3"
                    motivationAnimation.visibility = View.VISIBLE
                    motivationAnimation.setAnimation("fireworks.json")
                    motivationAnimation.playAnimation()
                }
            }

            motivationMessage.text = message
            motivationBox.visibility = View.VISIBLE

            val assetFileDescriptor = requireContext().assets.openFd(soundFile)
            mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                prepare()
                start()
            }

            closeMotivationButton.setOnClickListener {
                motivationBox.visibility = View.GONE
                motivationAnimation.cancelAnimation()
                motivationAnimation.visibility = View.GONE
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }
}

