package com.example.pianosense

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        Glide.with(this).asGif().load(R.drawable.sense).into(logoImageView)

        FirebaseAuth.getInstance().signOut()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Giriş yapılmışsa direkt ana sayfaya geç
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra("SKIP_ONBOARDING", true)
                })
            } else {
                // Giriş yapılmamışsa onboarding ekranına geç
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 4000)
    }
}
