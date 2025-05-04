package com.example.pianosense

import MusicViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.viewpager2.widget.ViewPager2
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnNextClickListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isOnboardingComplete = false

    // ViewModel
    private val musicViewModel: MusicViewModel by viewModels()

    // Pending deep link (QR koddan gelen) müzik ID'si
    private var pendingMusicId: Int? = null

    private val TAG = "DeepLinkMain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Deep link kontrolü: Önce intent.data'yı kontrol ediyoruz.
        if (intent.data != null) {
            val deepLinkData: Uri = intent.data!!
            Log.d(TAG, "Deep link detected in onCreate: $deepLinkData")
            val musicIdString = deepLinkData.getQueryParameter("musicId")
            if (!musicIdString.isNullOrEmpty()) {
                val musicId = musicIdString.toIntOrNull()
                if (musicId != null) {
                    pendingMusicId = musicId
                    Log.d(TAG, "Pending deep link set with musicId: $musicId from data URI")
                } else {
                    Log.e(TAG, "Failed to parse musicId from deep link data: $musicIdString")
                }
            } else {
                Log.e(TAG, "musicId parameter is empty in deep link data")
            }
        } else if (intent.hasExtra("pendingMusicId")) {
            // Eğer data URI yoksa, extra "pendingMusicId"'yi kontrol et (LoginActivity'den geliyor)
            val extraId = intent.getIntExtra("pendingMusicId", -1)
            if (extraId != -1) {
                pendingMusicId = extraId
                Log.d(TAG, "Pending deep link set with musicId: $extraId from intent extra")
            } else {
                Log.d(TAG, "No valid pendingMusicId extra found")
            }
        } else {
            Log.d(TAG, "No deep link found in onCreate")
        }

        // Onboarding kontrolü
        isOnboardingComplete = intent.getBooleanExtra("SKIP_ONBOARDING", false)
        Log.d(TAG, "isOnboardingComplete: $isOnboardingComplete")

        // Varsayılan olarak HomeFragment'i yükle
        showMainContent()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Log.d(TAG, "Bottom nav: Home selected")
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_search -> {
                    Log.d(TAG, "Bottom nav: Search selected")
                    loadFragment(SearchFragment())
                    true
                }
                R.id.navigation_play -> {
                    Log.d(TAG, "Bottom nav: Play selected")
                    loadFragment(PlayFragment())
                    true
                }
                R.id.navigation_saved -> {
                    Log.d(TAG, "Bottom nav: Saved selected")
                    loadFragment(SavedFragment())
                    true
                }
                R.id.navigation_settings -> {
                    Log.d(TAG, "Bottom nav: Settings selected")
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called with intent: $intent")
        intent.data?.let { deepLinkData ->
            Log.d(TAG, "Deep link detected in onNewIntent: $deepLinkData")
            val musicIdString = deepLinkData.getQueryParameter("musicId")
            if (!musicIdString.isNullOrEmpty()) {
                val musicId = musicIdString.toIntOrNull()
                if (musicId != null) {
                    pendingMusicId = musicId
                    Log.d(TAG, "Pending deep link updated with musicId: $musicId")
                } else {
                    Log.e(TAG, "Failed to parse musicId from deep link in onNewIntent: $musicIdString")
                }
            } else {
                Log.e(TAG, "musicId parameter is empty in deep link in onNewIntent")
            }
        }
    }

    /**
     * HomeFragment'i yükler. Ardından, pendingMusicId varsa,
     * 1 saniyelik gecikme sonrası PlayFragment'e yönlendirme yapar.
     */
    private fun showMainContent() {
        Log.d(TAG, "showMainContent: Loading HomeFragment")
        viewPager.visibility = View.GONE
        bottomNavigationView.visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

        // İlk olarak HomeFragment'i gösterelim
        loadFragment(HomeFragment())

        if (pendingMusicId != null) {
            Log.d(TAG, "Pending music ID exists: $pendingMusicId. Scheduling navigation to PlayFragment.")
            Handler(Looper.getMainLooper()).postDelayed({
                if (isUserLoggedIn()) {
                    val musicId = pendingMusicId!!
                    val music = musicViewModel.getMusicById(musicId)
                    if (music != null) {
                        Log.d(TAG, "Navigating to PlayFragment with music: $music")
                        // Set the selected music in the ViewModel (normal click flow benzeri)
                        musicViewModel.setSelectedMusic(music)
                        navigateToPlayFragmentWithMusic(music)
                    } else {
                        Log.e(TAG, "No music found for pending musicId: $musicId")
                    }
                } else {
                    Log.d(TAG, "User not logged in; redirecting to LoginActivity")
                    val loginIntent = Intent(this, LoginActivity::class.java).apply {
                        putExtra("pendingMusicId", pendingMusicId)
                    }
                    startActivity(loginIntent)
                    finish()
                    return@postDelayed
                }
                pendingMusicId = null
            }, 1000) // 1 saniyelik gecikme
        } else {
            Log.d(TAG, "No pending deep link to process")
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val loggedIn = FirebaseAuth.getInstance().currentUser != null
        Log.d(TAG, "isUserLoggedIn: $loggedIn")
        return loggedIn
    }

    override fun onNextClick() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < (viewPager.adapter?.itemCount ?: 0)) {
            viewPager.setCurrentItem(nextItem, true)
        } else {
            Log.d(TAG, "Onboarding finished, navigating to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        Log.d(TAG, "Loading fragment: ${fragment.javaClass.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToPlayFragmentWithMusic(music: Music) {
        Log.d(TAG, "navigateToPlayFragmentWithMusic called with music: $music")
        // Normal akışta kullanılan metot gibi çalışıyor:
        musicViewModel.setSelectedMusic(music)
        val playFragment = PlayFragment().apply {
            arguments = Bundle().apply {
                putInt("musicId", music.id)
                putString("title", music.title)
                putString("composer", music.composer)
                putInt("imageResId", music.imageResId)
                putString("audioFile", music.audioFilePath)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, playFragment)
            .addToBackStack(null)
            .commit()
        bottomNavigationView.selectedItemId = R.id.navigation_play
    }

    fun navigateToPlayFragment() {
        Log.d(TAG, "navigateToPlayFragment called")
        val playFragment = PlayFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, playFragment)
            .commit()
        bottomNavigationView.selectedItemId = R.id.navigation_play
    }

    //rol bilgisi için yeni eklendi
    private fun checkUserRoleAndNavigate() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid

        val databaseRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users").child(userId)

        databaseRef.child("rol").get().addOnSuccessListener { snapshot ->
            val rol = snapshot.getValue(String::class.java)
            if (rol == "ogretmen") {
                // Öğretmen paneline yönlendir
                val intent = Intent(this, OgretmenPanelActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Öğrenci ise normal akışa devam
                showMainContent()
            }
        }.addOnFailureListener {
            // Hata olursa yine normal akış
            showMainContent()
        }
    }

}
