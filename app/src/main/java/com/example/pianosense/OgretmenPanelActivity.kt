package com.example.pianosense

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OgretmenPanelActivity : AppCompatActivity() {

    private lateinit var editTextClassName: EditText
    private lateinit var buttonCreateClass: Button
    private lateinit var textViewClassCode: TextView
    private lateinit var teacherUid: String
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogretmen_panel)


        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // geri çıkma işlemi
        }


        editTextClassName = findViewById(R.id.editTextClassName)
        buttonCreateClass = findViewById(R.id.buttonCreateClass)
        textViewClassCode = findViewById(R.id.textViewClassCode)

        teacherUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(teacherUid)

        // Zaten bir classCode varsa doğrudan fragmenti aç
        databaseRef.child("classCode").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classCode = snapshot.getValue(String::class.java)
                if (!classCode.isNullOrEmpty()) {
                    textViewClassCode.text = "Sınıf Kodunuz: $classCode"
                    openStudentListFragment(classCode)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OgretmenPanelActivity, "Veri alınamadı", Toast.LENGTH_SHORT).show()
            }
        })

        // Yeni sınıf oluştur
        buttonCreateClass.setOnClickListener {
            val className = editTextClassName.text.toString().trim()
            if (className.isNotEmpty()) {
                createClassInFirebase(className)
            } else {
                Toast.makeText(this, "Sınıf adı boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createClassInFirebase(className: String) {
        val classCode = generateClassCode()
        val teacherId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val classData = mapOf(
            "className" to className,
            "teacherId" to teacherId
        )

        val databaseRef = FirebaseDatabase.getInstance().getReference("classes")
        databaseRef.child(classCode).setValue(classData)
            .addOnSuccessListener {
                textViewClassCode.text = "Sınıf Kodu: $classCode"
                Toast.makeText(this, "Sınıf başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show()

                // Kullanıcının profil verisine classCode'u da kaydet
                FirebaseDatabase.getInstance().getReference("users")
                    .child(teacherId)
                    .child("classCode")
                    .setValue(classCode)

                openStudentListFragment(classCode)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sınıf oluşturulamadı: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openStudentListFragment(classCode: String) {
        val fragment = TeacherStudentListFragment()
        val bundle = Bundle()
        bundle.putString("class_code", classCode)
        fragment.arguments = bundle

        supportFragmentManager.commit {
            replace(R.id.teacherPanelContainer, fragment)
        }
    }

    private fun generateClassCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
