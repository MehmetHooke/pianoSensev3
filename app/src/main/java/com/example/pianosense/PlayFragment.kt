package com.example.pianosense

import android.graphics.BitmapFactory
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
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment

class PlayFragment : Fragment() {

    companion object {
        private const val ARG_MUSIC_ID = "musicId"
        private const val ARG_MUSIC_TITLE = "musicTitle"
        private const val ARG_MUSIC_COMPOSER = "musicComposer"

        // newInstance fonksiyonunda argümanları Bundle ile fragmente geçiriyoruz
        fun newInstance(id: Int, title: String, composer: String): PlayFragment {
            val fragment = PlayFragment()
            val args = Bundle()
            args.putInt(ARG_MUSIC_ID, id)
            args.putString(ARG_MUSIC_TITLE, title)
            args.putString(ARG_MUSIC_COMPOSER, composer)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Müzik bilgilerini arguments'tan alıyoruz
        val musicTitle = arguments?.getString(ARG_MUSIC_TITLE)
        val musicComposer = arguments?.getString(ARG_MUSIC_COMPOSER)
        val musicId = arguments?.getInt(ARG_MUSIC_ID)

        Log.d("PlayFragment", "Received arguments - Music Title: $musicTitle, Composer: $musicComposer")


        // Müzik bilgilerini UI bileşenlerine aktarıyoruz
        view.findViewById<TextView>(R.id.musicTitle).text = musicTitle
        view.findViewById<TextView>(R.id.musicComposer).text = musicComposer

        val playMusicButton = view.findViewById<Button>(R.id.playMusicButton)
        val startPracticeButton = view.findViewById<Button>(R.id.startPracticeButton)

        playMusicButton.setOnClickListener {
            Toast.makeText(requireContext(), "Playing Music", Toast.LENGTH_SHORT).show()
        }

        startPracticeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Starting Practice", Toast.LENGTH_SHORT).show()
        }



        // Müzik resmi için ID'ye göre resim seçimi
        val musicSheetImageView = view.findViewById<ImageView>(R.id.musicSheetImage)
        val imageResId = when (musicId) {
            1 -> R.drawable.vals_evgeny_grinko
            2 -> R.drawable.vals_evgeny_grinko // Moonlight Sonata için özel resim
            3 -> R.drawable.vals_evgeny_grinko// Für Elise için özel resim
            else -> R.drawable.vals_evgeny_grinko // Varsayılan resim
        }
        musicSheetImageView.setImageResource(imageResId)

        // Görselin genişlik ayarları
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, imageResId, options)
        val imageWidth = options.outWidth

        // ImageView genişliğini bitmap genişliği ile sınırlayın
        musicSheetImageView.updateLayoutParams {
            width = imageWidth
        }

        musicSheetImageView.scaleType = ImageView.ScaleType.FIT_XY

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        if (imageWidth > screenWidth) {
            musicSheetImageView.updateLayoutParams {
                width = imageWidth
            }
        } else {
            musicSheetImageView.updateLayoutParams {
                width = screenWidth
            }
        }
    }
}
