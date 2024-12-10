package com.example.pianosense

import MusicViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.viewpager2.widget.ViewPager2
import androidx.activity.viewModels
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator

class MainActivity : AppCompatActivity(), OnNextClickListener {



    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isOnboardingComplete = false

    //yeni
    private val musicViewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //AndroidFFMPEGLocator(this)

        viewPager = findViewById(R.id.viewPager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // SKIP_ONBOARDING bilgisine göre işlem yap
        isOnboardingComplete = intent.getBooleanExtra("SKIP_ONBOARDING", false)

        if (isOnboardingComplete) {
            // Onboarding atlandı, ana sayfayı göster
            showMainContent()
        } else {
            // Onboarding ekranlarını göster
            setupOnboarding()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.navigation_play -> {
                    loadFragment(PlayFragment())
                    /*navigateToPlayFragment(
                        id = 1,
                        title = "Valse",
                        composer = "Evgeny Grinko",
                        imageResId = R.drawable.vals_evgeny_grinko
                    )*/
                    true
                }
                R.id.navigation_saved -> {
                    loadFragment(SavedFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

    }

    private fun setupOnboarding() {
        val adapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
        findViewById<View>(R.id.fragment_container).visibility = View.GONE // Fragment container başlangıçta gizli
    }

    private fun showMainContent() {
        viewPager.visibility = View.GONE
        bottomNavigationView.visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE // Fragment container görünür yap
        loadFragment(HomeFragment())
    }

    override fun onNextClick() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < viewPager.adapter?.itemCount ?: 0) {
            viewPager.setCurrentItem(nextItem, true)
        } else {
            // Onboarding tamamlandı, login ekranına yönlendir
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // HomeFragment'ten PlayFragment'e geçişi sağlamak için bu metodu ekledik
    /*
    fun navigateToPlayFragment(id: Int, title: String, composer: String, imageResId: Int) {
        Log.d("MainActivity", "Navigating to PlayFragment with title: $title, composer: $composer")
        val playFragment = PlayFragment.newInstance(id, title, composer, imageResId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, playFragment)
            .addToBackStack(null)
            .commit()



    }*/

    //yeninavigate fonk
    fun navigateToPlayFragment() {
        val playFragment = PlayFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, playFragment)
            .addToBackStack(null)
            .commit()

        bottomNavigationView.selectedItemId = R.id.navigation_play
    }

}
