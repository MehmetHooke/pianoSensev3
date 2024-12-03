package com.example.pianosense

import MusicViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PlayFragment : Fragment() {

    private val musicViewModel: MusicViewModel by activityViewModels()



    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicList: List<Music>
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private lateinit var audioRecord: AudioRecord
    private var isRecording = false
    private lateinit var audioFilePath: String
    private lateinit var wavFilePath: String
    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private val soundAnalysisService by lazy { SoundAnalysisService(requireContext()) }
    private lateinit var liveAnalysisTextView: TextView
    private var isLiveAnalysisRunning = false
    private var liveAudioDispatcher: AudioDispatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI bileşenlerini tanımlıyoruz
        val musicTitleTextView = view.findViewById<TextView>(R.id.musicTitle)
        val musicComposerTextView = view.findViewById<TextView>(R.id.musicComposer)
        val musicSheetImageView = view.findViewById<ImageView>(R.id.musicSheetImage)
        val startRecordButton = view.findViewById<Button>(R.id.startRecordButton)
        val listenRecordButton = view.findViewById<Button>(R.id.listenRecord)
        val resultsButton = view.findViewById<Button>(R.id.resultsButton)




        startRecordButton.setOnClickListener {
            if (checkMicrophonePermission()) {
                if (isLiveAnalysisRunning) {
                    Toast.makeText(requireContext(), "Live analysis is already running", Toast.LENGTH_SHORT).show()
                } else {
                    startLiveAudioAnalysis()
                    startRecordButton.text = "Live Analysis Running"
                }
            } else {
                requestMicrophonePermission()
            }
        }

        // Canlı ses analizini durdurma
        listenRecordButton.setOnClickListener {
            if (isLiveAnalysisRunning) {
                stopLiveAudioAnalysis()
                startRecordButton.text = "Start Live Analysis"
            } else {
                Toast.makeText(requireContext(), "Live analysis is not running", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Canlı ses analizi başlat
    private fun startLiveAudioAnalysis() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2
        //burası ile mikrofondan gelen veriler dispacther ile alıyoruz
        liveAudioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0)

        val pitchDetectionHandler = PitchDetectionHandler { result, _ ->
            val pitchInHz = result.pitch
            if (pitchInHz > 0) {
                val closestNote = soundAnalysisService.findClosestNoteJava(pitchInHz)
                requireActivity().runOnUiThread {
                    //liveAnalysisTextView?.text = "Frekans: $pitchInHz Hz, Nota: $closestNote"
                    Log.d("LiveAnalysis", "Frekans: $pitchInHz Hz, Nota: $closestNote") // Daha detaylı bilgi için

                }
            }
        }

        val pitchProcessor = PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.YIN,
            sampleRate.toFloat(),
            bufferSize,
            pitchDetectionHandler
        )

        liveAudioDispatcher?.addAudioProcessor(pitchProcessor)

        Thread {
            isLiveAnalysisRunning = true
            try {
                while (isLiveAnalysisRunning) {
                    liveAudioDispatcher?.run()
                }
            } catch (e: Exception) {
                Log.e("LiveAnalysis", "Hata: ${e.message}")
            }
        }.start()

        Toast.makeText(requireContext(), "Live audio analysis started", Toast.LENGTH_SHORT).show()
    }

    // Canlı analizi durdur
    private fun stopLiveAudioAnalysis() {
        isLiveAnalysisRunning = false
        liveAudioDispatcher?.stop()
        Toast.makeText(requireContext(), "Live audio analysis stopped", Toast.LENGTH_SHORT).show()
    }


    private fun checkMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMicrophonePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    private fun setMusicImageWidth(imageView: ImageView, imageResId: Int) {
        // Görüntü genişliğini ekran boyutuna göre ayarlıyoruz
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, imageResId, options)
        val imageWidth = options.outWidth

        imageView.updateLayoutParams {
            width = imageWidth
        }
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        if (imageWidth > screenWidth) {
            imageView.updateLayoutParams { width = imageWidth }
        } else {
            imageView.updateLayoutParams { width = screenWidth }
        }
    }



}
