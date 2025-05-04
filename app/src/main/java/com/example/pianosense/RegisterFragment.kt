package com.example.pianosense

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val fullNameEditText = view.findViewById<EditText>(R.id.fullNameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val phoneEditText = view.findViewById<EditText>(R.id.phoneEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val termsCheckBox = view.findViewById<CheckBox>(R.id.termsCheckBox)
        val registerButton = view.findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid ?: "default_user"

            val sharedPref = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE)
            sharedPref.edit().putString("user_name_$userId", fullName).apply()

            if (email.isNotEmpty() && password.isNotEmpty() && termsCheckBox.isChecked) {
                registerUser(email, password, fullName, phone)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and agree to terms", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, fullname: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid

                                if (userId != null) {
                                    val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                                    val userMap = hashMapOf(
                                        "name" to fullname,
                                        "email" to email,
                                        "phone" to phone,
                                        "rol" to "ogrenci" // 👈 rol alanı eklendi
                                    )

                                    databaseRef.child(userId).setValue(userMap)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(requireContext(), "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }

                                Toast.makeText(requireContext(), "Registration successful! Please check your email for verification.", Toast.LENGTH_LONG).show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, LoginFragment())
                                    .commit()
                            } else {
                                Toast.makeText(requireContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException ->
                            Toast.makeText(requireContext(), "User already exists!", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException ->
                            Toast.makeText(requireContext(), "Invalid email format!", Toast.LENGTH_SHORT).show()
                        else ->
                            Toast.makeText(requireContext(), "Registration failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
