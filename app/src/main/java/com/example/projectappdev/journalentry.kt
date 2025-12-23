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

        // 2. Retrieve Data passed from Previous Activity
        val dateText = intent.getStringExtra("datetext") ?: ""
        val timeText = intent.getStringExtra("timetext") ?: ""
        val moodText = intent.getStringExtra("moodtext") ?: ""
        val moodIcon = intent.getStringExtra("moodicon") ?: ""
        val moodColor = intent.getStringExtra("moodcolor") ?: "#000000"
        val weatherText = intent.getStringExtra("weathertext") ?: ""
        val weatherIcon = intent.getStringExtra("weathericon") ?: ""

        // 3. Setup UI Elements
        val tvDateDisplay = findViewById<TextView>(R.id.tvDateDisplay)
        val imgMood = findViewById<ImageView>(R.id.imgSelectedMood)
        val imgWeather = findViewById<ImageView>(R.id.imgSelectedWeather)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val etContent = findViewById<EditText>(R.id.etContent)

        // Note: In your latest XML, you changed the ID to btnSave and it is a Button
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 4. Update UI with passed data
        tvDateDisplay.text = "$dateText • $timeText"
        setDynamicImage(imgMood, moodIcon, moodColor)
        setDynamicImage(imgWeather, weatherIcon, null)

        // 5. Back Button Logic
        btnBack.setOnClickListener { finish() }

        // 6. SAVE BUTTON LOGIC
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val content = etContent.text.toString().trim()

            // Get Current User
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Check if fields are empty
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    saveToFirestore(
                        currentUser.uid, // Pass the User UID here
                        title, location, content,
                        dateText, timeText, moodText, moodIcon, moodColor, weatherText, weatherIcon
                    )
                } else {
                    Toast.makeText(this, "Please enter a title and content", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
                // Optional: Redirect to login page here if needed
            }
        }
    }

    private fun saveToFirestore(
        userUid: String, // Receive UID
        title: String, location: String, content: String,
        date: String, time: String, mood: String, moodIcon: String, moodColor: String, weather: String, weatherIcon: String
    ) {
        // Create Data Map
        val journalEntry = hashMapOf(
            "entryBy" to userUid, // Save the User UID!
            "title" to title,
            "location" to location,
            "content" to content,
            "date" to date,
            "time" to time,
            "mood" to mood,
            "moodicon" to moodIcon,
            "moodcolor" to moodColor,
            "weather" to weather,
            "weathericon" to weatherIcon,
            "timestamp" to System.currentTimeMillis() // Good for sorting by "newest"
        )

        // Upload to "journals" collection
        db.collection("tbl_entries")
            .add(journalEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Entry Saved!", Toast.LENGTH_SHORT).show()

                // Return to Home (MainActivity)
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setDynamicImage(imageView: ImageView, iconName: String, hexColor: String?) {
        if (iconName.isNotEmpty()) {
            val resourceId = resources.getIdentifier(iconName, "drawable", packageName)
            if (resourceId != 0) {
                imageView.setImageResource(resourceId)

                // If a color is provided, apply it
                if (hexColor != null) {
                    try {
                        imageView.setColorFilter(Color.parseColor(hexColor))
                    } catch (e: Exception) {
                        e.printStackTrace() // Fallback if hex code is invalid
                    }
                }
            }
        }
    }
}