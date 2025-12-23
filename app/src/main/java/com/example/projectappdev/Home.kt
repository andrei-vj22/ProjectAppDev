package com.example.projectappdev

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.format.DateFormat
import android.widget.*
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

        conn.collection("tbl_entries").get().addOnSuccessListener { records ->
            // Important: Clear the container if you plan to refresh this data
            vlayout.removeAllViews()

            for (record in records) {
                val inflater = LayoutInflater.from(this)
                val template = inflater.inflate(R.layout.activity_samplecard, vlayout, false)

                // 1. Reference the views in your template
                val txtDate: TextView = template.findViewById(R.id.txtDateejbvj)
                val imgMood: ImageView = template.findViewById(R.id.imgMoodejbvj)
                val txtTitle: TextView = template.findViewById(R.id.txtTitleejbvj)
                val txtWeather: TextView = template.findViewById(R.id.txtWeatherejbvj)
                val txtLoc: TextView = template.findViewById(R.id.txtLocejbvj)
                val txtTimestamp: TextView = template.findViewById(R.id.txtTimeejbvj)

                // 2. Get data from the Firestore document
                val title = record.getString("title") ?: "Untitled"
                val weather = record.getString("weather") ?: ""
                val location = record.getString("location") ?: ""

                // This is the string filename from your database (e.g., "sunny_mood")
                val moodSrcName = record.getString("moodSrc")

                // 3. Set the text views
                txtTitle.text = title
                txtWeather.text = weather
                txtLoc.text = location

                // 4. Dynamic Image Logic
                if (!moodSrcName.isNullOrEmpty()) {
                    // This finds the R.drawable ID by its string name
                    val resID = resources.getIdentifier(moodSrcName, "drawable", packageName)

                    if (resID != 0) {
                        imgMood.setImageResource(resID)
                    } else {
                        // Fallback if the string in DB doesn't match a file in drawable folder
                        imgMood.setImageResource(R.drawable.outline_mood_24)
                    }
                }

                // 5. Finally, add the filled template to your layout
                vlayout.addView(template)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
        }

        imgProf.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}