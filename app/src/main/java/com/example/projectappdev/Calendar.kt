package com.example.projectappdev

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class Calendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Initialize Firestore and views
        val db = FirebaseFirestore.getInstance()
        val totalJournalsTxt: TextView = findViewById(R.id.textView12)
        val btnBack: ImageView = findViewById(R.id.img_goback2)

        // 2. Fetch the count from tbl_entries
        db.collection("tbl_entries")
            .get()
            .addOnSuccessListener { result ->
                // The size of the snapshot is the number of documents
                val count = result.size()
                totalJournalsTxt.text = count.toString()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                totalJournalsTxt.text = "0"
            }

        // Back button logic
        btnBack.setOnClickListener {
            finish()
        }
    }
}