package com.example.projectappdev

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_mwselection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvDate = findViewById<TextView>(R.id.tvCurrentDate)

        tvDate.text = DateFormat.format("MMMM dd, yyyy", calendar)

        tvDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)

                tvDate.text = DateFormat.format("MMMM dd, yyyy", calendar)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val tvTime = findViewById<TextView>(R.id.tvCurrentTime)

        tvTime.text = DateFormat.format("hh:mm aa", calendar)

        tvTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                tvTime.text = DateFormat.format("hh:mm aa", calendar)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show() // 'false' = 12-hour picker
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupMoodListeners()
        setupWeatherListeners()

        findViewById<Button>(R.id.btnNext).setOnClickListener {
            if (selectedMoodText != null && selectedWeatherText != null) {
                val intent = Intent(this, journalentry::class.java)
                intent.putExtra("mood", selectedMoodText)
                intent.putExtra("moodicon", selectedMoodIcon)
                intent.putExtra("moodcolor", selectedMoodColor)
                intent.putExtra("weather", selectedWeatherText)
                intent.putExtra("weathericon", selectedWeatherIcon)
                intent.putExtra("date", tvDate.text.toString())
                intent.putExtra("time", tvTime.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Must Select Mood & Weather", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMoodListeners() {
        val moodLayouts = listOf(
            findViewById<LinearLayout>(R.id.btnMoodSplendidejbvj),
            findViewById<LinearLayout>(R.id.btnMoodGoodejbvj),
            findViewById<LinearLayout>(R.id.btnMoodMehejbvj),
            findViewById<LinearLayout>(R.id.btnMoodBadejbvj),
            findViewById<LinearLayout>(R.id.btnMoodAwfulejbvj)
        )

        fun selectMood(layout: LinearLayout, text: String, icon: String, hexColor: String) {
            selectedMoodText = text
            selectedMoodIcon = icon
            selectedMoodColor = hexColor

            moodLayouts.forEach { it.alpha = 0.4f }
            layout.alpha = 1.0f
        }

        moodLayouts[0].setOnClickListener {
            selectMood(it as LinearLayout, "Splendid", "verygood", "#4CAF50")
        }
        moodLayouts[1].setOnClickListener {
            selectMood(it as LinearLayout, "Good", "outline_mood_24", "#8BC34A")
        }
        moodLayouts[2].setOnClickListener {
            selectMood(it as LinearLayout, "Meh", "midatbest", "#FFC107")
        }
        moodLayouts[3].setOnClickListener {
            selectMood(it as LinearLayout, "Bad", "moodbad", "#FF9800")
        }
        moodLayouts[4].setOnClickListener {
            selectMood(it as LinearLayout, "Awful", "verybad", "#F44336")
        }
    }

    private fun setupWeatherListeners() {
        val weatherLayouts = listOf(
            findViewById<LinearLayout>(R.id.btnWeatherSunnyejbvj),
            findViewById<LinearLayout>(R.id.btnWeatherCloudsejbvj),
            findViewById<LinearLayout>(R.id.btnWeatherRainejbvj),
            findViewById<LinearLayout>(R.id.btnWeatherSnowejbvj),
            findViewById<LinearLayout>(R.id.btnWeatherStormejbvj)
        )

        fun selectWeather(layout: LinearLayout, text: String, icon: String) {
            selectedWeatherText = text
            selectedWeatherIcon = icon

            weatherLayouts.forEach { it.alpha = 0.4f }
            layout.alpha = 1.0f
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