package com.example.pianosense

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        Glide.with(this).asGif().load(R.drawable.sense).into(logoImageView)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            val deepLinkData: Uri? = intent.data

            if (deepLinkData != null) {
                Log.d(TAG, "Deep link found: ${deepLinkData.toString()}")
                // Deep link mevcut: pianosense://play?musicId=...
                if (currentUser == null) {
                    // Giriş yapılmamışsa, LoginActivity'ye pendingMusicId ile yönlendir
                    val loginIntent = Intent(this, LoginActivity::class.java).apply {
                        putExtra("pendingMusicId", deepLinkData.getQueryParameter("musicId")?.toIntOrNull() ?: -1)
                    }
                    Log.d(TAG, "User not logged in. Redirecting to LoginActivity with pendingMusicId.")
                    startActivity(loginIntent)
                } else {
                    // Giriş yapılmışsa, deep link verisini MainActivity'ye aktar
                    val mainIntent = Intent(this, MainActivity::class.java).apply {
                        data = deepLinkData
                        putExtra("SKIP_ONBOARDING", true)
                    }
                    Log.d(TAG, "User logged in. Redirecting to MainActivity with deep link data.")
                    startActivity(mainIntent)
                }
            } else {
                // Deep link yoksa
                if (currentUser == null) {
                    Log.d(TAG, "No deep link & user not logged in. Redirecting to LoginActivity.")
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Log.d(TAG, "No deep link & user is logged in. Redirecting to MainActivity.")
                    val mainIntent = Intent(this, MainActivity::class.java).apply {
                        putExtra("SKIP_ONBOARDING", true)
                    }
                    startActivity(mainIntent)
                }
            }
            finish()
        }, 4500)
    }
}
