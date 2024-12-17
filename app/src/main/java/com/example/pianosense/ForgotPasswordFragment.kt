package com.example.pianosense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        val emailField = view.findViewById<EditText>(R.id.emailField)
        val resetPasswordButton = view.findViewById<Button>(R.id.changePasswordButton)

        // Şifre sıfırlama butonuna tıklama işlemi
        resetPasswordButton.setOnClickListener {
            val email = emailField.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(context, "Lütfen e-posta adresinizi girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendPasswordResetEmail(email)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()

        }
    }
    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Şifre sıfırlama e-postası gönderildi. Lütfen e-postanızı kontrol edin.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        // Bu hata, e-posta adresinin Firebase'de bulunmadığını gösterir
                        Toast.makeText(context, "Böyle bir kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Diğer hata türlerini ele al
                        val errorMessage = exception?.localizedMessage ?: "Bir hata oluştu."
                        Toast.makeText(context, "Hata: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


}