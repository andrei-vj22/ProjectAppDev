package com.example.projectappdev

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.*
import android.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class journalentry : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journalentry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val entryId: String? = intent.getStringExtra("entryid")
        val existingTitle = intent.getStringExtra("title")
        val existingContent = intent.getStringExtra("content")
        val existingLocation = intent.getStringExtra("location")

        val dateText = intent.getStringExtra("date") ?: ""
        val timeText = intent.getStringExtra("time") ?: ""
        val moodText = intent.getStringExtra("mood") ?: ""
        val moodIcon = intent.getStringExtra("moodicon") ?: ""
        val moodColor = intent.getStringExtra("moodcolor") ?: "#000000"
        val weatherText = intent.getStringExtra("weather") ?: ""
        val weatherIcon = intent.getStringExtra("weathericon") ?: ""

        val tvDateDisplay = findViewById<TextView>(R.id.tvDateDisplayejbvj)
        val imgMood = findViewById<ImageView>(R.id.imgSelectedMoodejbvj)
        val imgWeather = findViewById<ImageView>(R.id.imgSelectedWeatherejbvj)

        val etTitle = findViewById<EditText>(R.id.etTitleejbvj)
        val etLocation = findViewById<EditText>(R.id.etLocationejbvj)
        val etContent = findViewById<EditText>(R.id.etContentejbvj)

        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        if (entryId != null) {
            etTitle.setText(existingTitle)
            etContent.setText(existingContent)
            etLocation.setText(existingLocation)

            findViewById<TextView>(R.id.tvHeaderejbvj).text = "Edit Entry"
        }

        tvDateDisplay.text = "$dateText • $timeText"
        setDynamicImage(imgMood, moodIcon, moodColor)
        setDynamicImage(imgWeather, weatherIcon, null)

        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val content = etContent.text.toString().trim()

            val currentUser = auth.currentUser

            if (currentUser != null) {
                if (title.isNotEmpty() && content.isNotEmpty()) {

                    val journalData = hashMapOf(
                        "entryBy" to currentUser.uid,
                        "title" to title,
                        "location" to location,
                        "content" to content,
                        "date" to dateText,
                        "time" to timeText,
                        "mood" to moodText,
                        "moodicon" to moodIcon,
                        "moodcolor" to moodColor,
                        "weather" to weatherText,
                        "weathericon" to weatherIcon,
                        "timestamp" to System.currentTimeMillis()
                    )

                    if (entryId != null) {
                        db.collection("tbl_entries").document(entryId!!).set(journalData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Entry Updated!", Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent(this, Home::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error updating: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {
                        db.collection("tbl_entries").add(journalData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Entry Saved!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Home::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error saving: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                } else {
                    Toast.makeText(this, "Please enter a title and content", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun setDynamicImage(imageView: ImageView, iconName: String, hexColor: String?) {
        if (iconName.isNotEmpty()) {
            val resourceId = resources.getIdentifier(iconName, "drawable", packageName)
            if (resourceId != 0) {
                imageView.setImageResource(resourceId)

                if (hexColor != null) {
                    try {
                        imageView.setColorFilter(Color.parseColor(hexColor))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}