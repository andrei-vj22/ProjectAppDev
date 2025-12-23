package com.example.projectappdev

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.format.DateFormat
import android.widget.*
import android.graphics.Color
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val conn = FirebaseFirestore.getInstance()
        val dateTextView: TextView = findViewById(R.id.txt_date)
        val formattedDate = DateFormat.format("EEEE, MMM d", System.currentTimeMillis())
        dateTextView.text = formattedDate
        val imgProf: ImageView = findViewById(R.id.imgProfile)

        val vlayout: LinearLayout = findViewById(R.id.linlayejbvj)
        val auth = FirebaseAuth.getInstance()


        val userId = auth.currentUser?.uid
        val greetingText: TextView = findViewById(R.id.textView2)


        //good day greeting
        if (userId != null) {
            conn.collection("tbl_users").document(userId).get()
                .addOnSuccessListener { record ->
                    if (record.exists()) {
                        // Fetch the full name from the database
                        val fullName = record.getString("name") ?: "User"

                        // Extract only the first name
                        val firstName = fullName.split(" ").firstOrNull() ?: "User"

                        // Update the UI
                        greetingText.text = "Good day, $firstName!"
                    }
                }
                .addOnFailureListener {
                    // Optional: fallback if the database call fails
                    greetingText.text = "Good day!"
                }
        }

// Note: Ensure collection name matches where you saved it (likely "journals" from previous steps)
        if (userId != null) {
            conn.collection("tbl_entries")
                .whereEqualTo("entryBy", userId) // Filter: Show only this user's entries
                .get()
                .addOnSuccessListener { records ->

                    vlayout.removeAllViews() // Clear old data to prevent duplicates

                    for (record in records) {
                        val inflater = LayoutInflater.from(this)
                        val template =
                            inflater.inflate(R.layout.activity_samplecard, vlayout, false)

                        // Reference Views
                        val txtDate: TextView = template.findViewById(R.id.txtDateejbvj)
                        val imgMood: ImageView = template.findViewById(R.id.imgMoodejbvj)
                        val txtTitle: TextView = template.findViewById(R.id.txtTitleejbvj)
                        val txtWeather: TextView = template.findViewById(R.id.txtWeatherejbvj)
                        val txtLoc: TextView = template.findViewById(R.id.txtLocejbvj)
                        val txtTimestamp: TextView = template.findViewById(R.id.txtTimeejbvj)

                        // Get Data from Firestore
                        val title = record.getString("title") ?: "Untitled"
                        val weather = record.getString("weather") ?: ""
                        val location = record.getString("location") ?: ""
                        val dateEntry = record.getString("date") ?: ""
                        val timeEntry = record.getString("time") ?: ""

                        // Icon & Color Logic
                        // NOTE: Make sure these field names match exactly what you saved in entrypage.kt
                        val moodSrcName = record.getString("moodicon")
                        val moodColorHex = record.getString("moodcolor") // Get Hex Color

                        // Set Text
                        txtTitle.text = title
                        txtWeather.text = weather
                        txtLoc.text = location
                        txtDate.text = dateEntry
                        txtTimestamp.text = timeEntry

                        // Dynamic Image & Color Tint Logic
                        if (!moodSrcName.isNullOrEmpty()) {
                            val resID =
                                resources.getIdentifier(moodSrcName, "drawable", packageName)

                            if (resID != 0) {
                                imgMood.setImageResource(resID)

                                // Apply Color Tint if hex is available
                                if (!moodColorHex.isNullOrEmpty()) {
                                    try {
                                        imgMood.setColorFilter(Color.parseColor(moodColorHex))
                                    } catch (e: IllegalArgumentException) {
                                        // Handle invalid hex code safely
                                        imgMood.clearColorFilter()
                                    }
                                } else {
                                    imgMood.clearColorFilter()
                                }
                            } else {
                                // Fallback image if resource not found
                                imgMood.setImageResource(R.drawable.outline_mood_24)
                            }
                        }

                        // Add to Layout
                        vlayout.addView(template)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }

        // --- 3. PROFILE NAVIGATION ---
        imgProf.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}