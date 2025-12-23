package com.example.projectappdev

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.*
import android.content.Intent
import android.text.format.DateFormat
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
class mwselection : AppCompatActivity() {
    private var selectedMoodText: String? = null
    private var selectedMoodIcon: String? = null
    private var selectedMoodColor: String? = null
    private var selectedWeatherText: String? = null
    private var selectedWeatherIcon: String? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mwselection)

        // Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- 1. DATE SETUP ---
        val tvDate = findViewById<TextView>(R.id.tvCurrentDate)

        // Initial text (Current Date): "January 21, 2025"
        tvDate.text = DateFormat.format("MMMM dd, yyyy", calendar)

        // Date Picker Listener
        tvDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                // 1. Update the calendar variable with user choice
                calendar.set(year, month, day)

                // 2. Format it using Android DateFormat and update TextView
                tvDate.text = DateFormat.format("MMMM dd, yyyy", calendar)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // --- 2. TIME SETUP (UPDATED) ---
        val tvTime = findViewById<TextView>(R.id.tvCurrentTime)

        // Change format to "hh:mm aa" (e.g., 01:30 PM)
        tvTime.text = DateFormat.format("hh:mm aa", calendar)

        tvTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                // Update calendar with new time
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                // Format automatically handles conversion to AM/PM
                tvTime.text = DateFormat.format("hh:mm aa", calendar)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show() // 'false' = 12-hour picker
        }

        // --- BACK BUTTON ---
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupMoodListeners()
        setupWeatherListeners()

        // Next Button
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            if (selectedMoodText != null && selectedWeatherText != null) {
                val intent = Intent(this, journalentry::class.java)
                intent.putExtra("moodtext", selectedMoodText)
                intent.putExtra("moodicon", selectedMoodIcon)
                intent.putExtra("moodcolor", selectedMoodColor)
                intent.putExtra("weathertext", selectedWeatherText)
                intent.putExtra("weathericon", selectedWeatherIcon)
                // Pass the strings directly from the TextViews
                intent.putExtra("datetext", tvDate.text.toString())
                intent.putExtra("timetext", tvTime.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Must Select Mood & Weather", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMoodListeners() {
        val moodLayouts = listOf(
            findViewById<LinearLayout>(R.id.btnMoodSplendid),
            findViewById<LinearLayout>(R.id.btnMoodGood),
            findViewById<LinearLayout>(R.id.btnMoodMeh),
            findViewById<LinearLayout>(R.id.btnMoodBad),
            findViewById<LinearLayout>(R.id.btnMoodAwful)
        )

        // Helper to update selection
        fun selectMood(layout: LinearLayout, text: String, icon: String, hexColor: String) {
            selectedMoodText = text
            selectedMoodIcon = icon
            selectedMoodColor = hexColor // Store it

            // Visual Feedback: Dim all, Highlight selected
            moodLayouts.forEach { it.alpha = 0.4f } // Dim others
            layout.alpha = 1.0f // Highlight selected
        }

        // [UPDATED] Pass the specific colors you used in XML
        moodLayouts[0].setOnClickListener { selectMood(it as LinearLayout, "Splendid", "verygood", "#4CAF50") }
        moodLayouts[1].setOnClickListener { selectMood(it as LinearLayout, "Good", "outline_mood_24", "#8BC34A") }
        moodLayouts[2].setOnClickListener { selectMood(it as LinearLayout, "Meh", "midatbest", "#FFC107") }
        moodLayouts[3].setOnClickListener { selectMood(it as LinearLayout, "Bad", "moodbad", "#FF9800") }
        moodLayouts[4].setOnClickListener { selectMood(it as LinearLayout, "Awful", "verybad", "#F44336") }
    }

    private fun setupWeatherListeners() {
        val weatherLayouts = listOf(
            findViewById<LinearLayout>(R.id.btnWeatherSunny),
            findViewById<LinearLayout>(R.id.btnWeatherClouds),
            findViewById<LinearLayout>(R.id.btnWeatherRain),
            findViewById<LinearLayout>(R.id.btnWeatherSnow),
            findViewById<LinearLayout>(R.id.btnWeatherStorm)
        )

        // Helper to update selection
        fun selectWeather(layout: LinearLayout, text: String, icon: String) {
            selectedWeatherText = text
            selectedWeatherIcon = icon

            // Visual Feedback: Dim all, Highlight selected
            weatherLayouts.forEach { it.alpha = 0.4f } // Dim others
            layout.alpha = 1.0f // Highlight selected
        }

        weatherLayouts[0].setOnClickListener {
            selectWeather(it as LinearLayout, "Sunny", "sunny")
        }
        weatherLayouts[1].setOnClickListener {
            selectWeather(it as LinearLayout, "Cloudy", "cloudy")
        }
        weatherLayouts[2].setOnClickListener {
            selectWeather(it as LinearLayout, "Rainy", "rainy")
        }
        weatherLayouts[3].setOnClickListener {
            selectWeather(it as LinearLayout, "Snowy", "snowy")
        }
        weatherLayouts[4].setOnClickListener {
            selectWeather(it as LinearLayout, "Stormy", "stormy")
        }
    }
}