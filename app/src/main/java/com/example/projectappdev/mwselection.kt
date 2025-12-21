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
class mwselection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mwselection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // --- BACK BUTTON LOGIC ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Returns to Home
        }

        // --- DATE & TIME LOGIC (Using android.text.format.DateFormat) ---
        val tvDate = findViewById<TextView>(R.id.tvCurrentDate)
        val tvTime = findViewById<TextView>(R.id.tvCurrentTime)

        // Get current system time
        val now = System.currentTimeMillis()

        // Format: "Today, 21 January"
        // "MMMM" = Full month name
        val dateString = "Today, " + DateFormat.format("d MMMM", now)

        // Format: "12:14"
        val timeString = DateFormat.format("HH:mm", now)

        tvDate.text = dateString
        tvTime.text = timeString

        // --- MOOD SELECTION LOGIC ---
        setupMoodListeners()
    }

    private fun setupMoodListeners() {
        val btnRad = findViewById<LinearLayout>(R.id.btnMoodSplendid)
        val btnGood = findViewById<LinearLayout>(R.id.btnMoodGood)
        val btnMeh = findViewById<LinearLayout>(R.id.btnMoodMeh)
        val btnBad = findViewById<LinearLayout>(R.id.btnMoodBad)
        val btnAwful = findViewById<LinearLayout>(R.id.btnMoodAwful)

        // 1. RAD Listener
        btnRad.setOnClickListener {
            // We pass "verygood" because that matches your drawable filename: @drawable/verygood
            goToJournalEntry("Splendid", "verygood")
        }

        // 2. GOOD Listener
        btnGood.setOnClickListener {
            // We pass "outline_mood_24" to match your drawable: @drawable/outline_mood_24
            goToJournalEntry("Good", "outline_mood_24")
        }

        // 3. MEH Listener
        btnMeh.setOnClickListener {
            goToJournalEntry("Meh", "midatbest")
        }

        // 4. BAD Listener
        btnBad.setOnClickListener {
            goToJournalEntry("Bad", "moodbad")
        }

        // 5. AWFUL Listener
        btnAwful.setOnClickListener {
            goToJournalEntry("Awful", "verybad")
        }
    }

    // Helper function to avoid rewriting the intent code 5 times
    private fun goToJournalEntry(moodText: String, iconName: String) {
        // REPLACE 'JournalEntryActivity::class.java' with the actual name of your next page
        //val intent = Intent(this, entrypage::class.java)

        // Pass the data
        intent.putExtra("MOOD_TEXT", moodText)
        intent.putExtra("MOOD_ICON_NAME", iconName)

        startActivity(intent)
    }
}