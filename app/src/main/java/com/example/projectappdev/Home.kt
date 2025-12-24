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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val conn = FirebaseFirestore.getInstance()
        val dateTextView: TextView = findViewById(R.id.txt_date)
        val formattedDate = DateFormat.format("EEEE, MMM d", System.currentTimeMillis())
        dateTextView.text = formattedDate
        val imgProf: ImageView = findViewById(R.id.imgProfile)

        val vlayout: LinearLayout = findViewById(R.id.linlayejbvj)
        val auth = FirebaseAuth.getInstance()

        val btnHome: ImageView = findViewById(R.id.btnHome)
        val btnAdd: ImageView = findViewById(R.id.btnAdd)
        val btnAnalytics: ImageView = findViewById(R.id.btnAnalytics)

        val userId = auth.currentUser?.uid
        val greetingText: TextView = findViewById(R.id.textView2)

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

        // Good day greeting
        if (userId != null) {
            conn.collection("tbl_users").document(userId).get()
                .addOnSuccessListener { record ->
                    if (record.exists()) {
                        val fullName = record.getString("name") ?: "User"
                        val firstName = fullName.split(" ").firstOrNull() ?: "User"
                        greetingText.text = "Good day, $firstName!"
                    }
                }
                .addOnFailureListener {
                    greetingText.text = "Good day!"
                }
        }

        // Fetch entries
        if (userId != null) {
            conn.collection("tbl_entries")
                .whereEqualTo("entryBy", userId)
                .get()
                .addOnSuccessListener { records ->
                    vlayout.removeAllViews()

                    for (record in records) {
                        val inflater = LayoutInflater.from(this)
                        val template = inflater.inflate(R.layout.activity_samplecard, vlayout, false)

                        val txtDate: TextView = template.findViewById(R.id.txtDateejbvj)
                        val imgMood: ImageView = template.findViewById(R.id.imgMoodejbvj)
                        val txtTitle: TextView = template.findViewById(R.id.txtTitleejbvj)
                        val txtWeather: TextView = template.findViewById(R.id.txtWeatherejbvj)
                        val txtLoc: TextView = template.findViewById(R.id.txtLocejbvj)
                        val txtTime: TextView = template.findViewById(R.id.txtTimeejbvj)

                        txtTitle.text = record.getString("title") ?: "Untitled"
                        txtWeather.text = record.getString("weather") ?: ""
                        txtLoc.text = record.getString("location") ?: ""
                        txtDate.text = record.getString("date") ?: ""
                        txtTime.text = record.getString("time") ?: ""

                        val moodSrcName = record.getString("moodicon")
                        val moodColorHex = record.getString("moodcolor")

                        if (!moodSrcName.isNullOrEmpty()) {
                            val resID = resources.getIdentifier(moodSrcName, "drawable", packageName)
                            if (resID != 0) {
                                imgMood.setImageResource(resID)
                                if (!moodColorHex.isNullOrEmpty()) {
                                    try {
                                        imgMood.setColorFilter(Color.parseColor(moodColorHex))
                                    } catch (e: Exception) {
                                        imgMood.clearColorFilter()
                                    }
                                }
                            }
                        }
                        vlayout.addView(template)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }

        imgProf.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}