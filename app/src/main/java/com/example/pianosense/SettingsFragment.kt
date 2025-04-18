package com.example.pianosense

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    //mikrofon için değişken tanımla
    private val REQUEST_MICROPHONE_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Authentication başlat
        auth = FirebaseAuth.getInstance()

        // Request Microphone Permission button
        val requestMicPermissionButton = view.findViewById<Button>(R.id.requestMicPermissionButton)
        requestMicPermissionButton.setOnClickListener {
            checkMicrophonePermission()
        }

        // Kullanıcı bilgilerini ayarlama
        val userNameTextView = view.findViewById<TextView>(R.id.userName)
        val userEmailTextView = view.findViewById<TextView>(R.id.userEmail)
        val registerDate = view.findViewById<TextView>(R.id.register_date)

        val userNameTextViewDetail = view.findViewById<TextView>(R.id.userNameDetail)
        val userEmailTextViewDetail = view.findViewById<TextView>(R.id.userEmailDetail)


// Kullanıcının UID'sini al
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            databaseRef.get().addOnSuccessListener { snapshot ->
                val name = snapshot.child("name").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)

                // Kullanıcı adını ve emaili ekrana göster
                userNameTextView.text = name ?: "Name Surname"
                userEmailTextView.text = email ?: currentUser.email
                userEmailTextViewDetail.text = "Mail: ${email ?: currentUser.email}"
                userNameTextViewDetail.text = "Name: ${name ?: "Name Surname"}"
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        }

        val user = auth.currentUser

        // Sayfa başlığını ayarla
        val pageTitle = view.findViewById<TextView>(R.id.pageTitle)
       pageTitle?.text = "Settings Page"

        // Çıkış yapma butonuna eriş ve tıklama olayını ayarla
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            auth.signOut()  // Firebase'den çıkış yap
            Log.d("Auth", "User signed out: ${FirebaseAuth.getInstance().currentUser}")
            // Giriş ekranına yönlendir
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // Ana sayfayı kapat
        }
        // Kullanıcının kayıt tarihini formatlayarak göster
        user?.metadata?.creationTimestamp?.let { timestamp ->
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            registerDate?.text = "Registration Date: $formattedDate"
        }

        // Açılabilir ayar başlıklarını ayarlama
        setupExpandableSections(view)


    }
    private fun setupExpandableSections(view: View) {
        // Personal Information bölümü
        val personalInfoHeader = view.findViewById<TextView>(R.id.personalInfoHeader)
        val personalInfoContent = view.findViewById<View>(R.id.personalInfoContent)
        personalInfoHeader.setOnClickListener {
            toggleVisibility(personalInfoContent)
        }

        // Privacy and Permissions bölümü
        val privacyHeader = view.findViewById<TextView>(R.id.privacyHeader)
        val privacyContent = view.findViewById<View>(R.id.privacyContent)
        privacyHeader.setOnClickListener {
            toggleVisibility(privacyContent)
        }


    }

    private fun toggleVisibility(content: View) {
        content.visibility = if (content.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun checkMicrophonePermission() {
        // Mikrofon izni kontrol edilir
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // İzin verilmemişse kullanıcıdan istenir
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_MICROPHONE_PERMISSION
            )
        } else {
            // İzin zaten verilmişse bilgi verilir
            Toast.makeText(requireContext(), "Microphone permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MICROPHONE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verilmişse kullanıcı bilgilendirilir
                Toast.makeText(requireContext(), "Microphone permission access", Toast.LENGTH_SHORT).show()
            } else {
                // İzin reddedilmişse kullanıcıya bilgi verilir
                Toast.makeText(requireContext(), "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
