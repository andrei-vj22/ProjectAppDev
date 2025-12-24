package com.example.projectappdev

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class Calendar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }


        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val totalJournalsTxt: TextView = findViewById(R.id.textView12)
        val btnBack: ImageView = findViewById(R.id.img_goback2)
        val barContainer: LinearLayout = findViewById(R.id.barContainer)


        val btnHome: ImageView = findViewById(R.id.btnHome2)
        val btnAdd: ImageView = findViewById(R.id.btnAdd2)
        val btnAnalytics: ImageView = findViewById(R.id.btnAnalytics2)

        btnBack.setOnClickListener { finish() }

        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        btnAdd.setOnClickListener {
            val intent = Intent(this, mwselection::class.java)
            startActivity(intent)
        }
        btnAnalytics.setOnClickListener {
            val intent = Intent(this, Calendar::class.java)
            startActivity(intent)
        }

        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("tbl_entries")
                .whereEqualTo("entryBy", userId)
                .get()
                .addOnSuccessListener { records ->
                    totalJournalsTxt.text = records.size().toString()

                    // Update UI components
                    drawManualGraphByDay(records, barContainer)
                    calculateMoodTotals(records)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    fun calculateMoodTotals(records: com.google.firebase.firestore.QuerySnapshot) {
        var rad = 0; var good = 0; var meh = 0; var bad = 0; var awful = 0

        val currentMonth = SimpleDateFormat("MMMM", Locale.US).format(java.util.Date())
        val currentYear = SimpleDateFormat("yyyy", Locale.US).format(java.util.Date())

        for (doc in records) {
            val dateStr = doc.getString("date") ?: ""
            if (dateStr.contains(currentMonth) && dateStr.contains(currentYear)) {
                when (doc.getString("moodicon")) {
                    "verygood" -> rad++
                    "outline_mood_24" -> good++
                    "midatbest" -> meh++
                    "moodbad" -> bad++
                    "verybad" -> awful++
                }
            }
        }

        findViewById<TextView>(R.id.tvCountSplendid).text = rad.toString()
        findViewById<TextView>(R.id.tvCountGood).text = good.toString()
        findViewById<TextView>(R.id.tvCountMeh).text = meh.toString()
        findViewById<TextView>(R.id.tvCountBad).text = bad.toString()
        findViewById<TextView>(R.id.tvCountAwful).text = awful.toString()
    }

    fun drawManualGraphByDay(records: com.google.firebase.firestore.QuerySnapshot, container: LinearLayout) {
        container.removeAllViews()
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val dayMap = daysOfWeek.associateWith { 0 }.toMutableMap()

        val currentMonth = SimpleDateFormat("MMMM", Locale.US).format(java.util.Date())
        val currentYear = SimpleDateFormat("yyyy", Locale.US).format(java.util.Date())
        val inputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val dayFormat = SimpleDateFormat("EEEE", Locale.US)

        for (doc in records) {
            val dateString = doc.getString("date") ?: ""
            if (dateString.contains(currentMonth) && dateString.contains(currentYear)) {
                try {
                    val parsedDate = inputFormat.parse(dateString)
                    parsedDate?.let {
                        val dayName = dayFormat.format(it)
                        dayMap[dayName] = dayMap.getOrDefault(dayName, 0) + 1
                    }
                } catch (e: Exception) { /* Skip invalid dates */ }
            }
        }

        val maxEntries = dayMap.values.maxOrNull()?.coerceAtLeast(1) ?: 1
        for (day in daysOfWeek) {
            val count = dayMap[day]!!
            val col = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }
            val bar = View(this).apply {
                val h = (count.toFloat() / maxEntries.toFloat() * 300).toInt().coerceAtLeast(10)
                layoutParams = LinearLayout.LayoutParams(40, h).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    setMargins(0, 0, 0, 10)
                }
                setBackgroundColor(if (count > 0) Color.parseColor("#DCB027") else Color.parseColor("#E0E0E0"))
            }
            val label = TextView(this).apply {
                text = day.substring(0, 3); textSize = 11f; gravity = Gravity.CENTER
            }
            col.addView(bar); col.addView(label); container.addView(col)
        }
    }
}