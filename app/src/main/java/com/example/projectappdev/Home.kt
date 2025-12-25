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
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        // UI References
        val dateTextView: TextView = findViewById(R.id.txt_date)
        val formattedDate = DateFormat.format("EEEE, MMM d", System.currentTimeMillis())
        dateTextView.text = formattedDate

        val imgProf: ImageView = findViewById(R.id.imgProfile)
        val vlayout: LinearLayout = findViewById(R.id.linlayejbvj) // The container for cards
        val greetingText: TextView = findViewById(R.id.textView2)

        // Navigation Buttons
        val btnHome: ImageView = findViewById(R.id.btnHome)
        val btnAdd: ImageView = findViewById(R.id.btnAdd)
        val btnAnalytics: ImageView = findViewById(R.id.btnAnalytics)

        // --- NAVIGATION LOGIC ---
        btnHome.setOnClickListener {
            // Already on Home, maybe scroll to top or refresh?
            startActivity(Intent(this, Home::class.java))
            overridePendingTransition(0,0) // Optional: disables animation for "refresh" feel
        }
        btnAdd.setOnClickListener {
            startActivity(Intent(this, mwselection::class.java))
        }
        btnAnalytics.setOnClickListener {
            startActivity(Intent(this, Calendar::class.java)) // Ensure you have a 'Calendar' activity
        }

        // --- GREETING LOGIC ---
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

        // --- FETCH & DISPLAY ENTRIES ---
        if (userId != null) {
            conn.collection("tbl_entries") // Matches your collection name
                .whereEqualTo("entryBy", userId)
                .get()
                .addOnSuccessListener { records ->

                    vlayout.removeAllViews() // Clear list to avoid duplicates on refresh

                    for (record in records) {
                        val inflater = LayoutInflater.from(this)
                        val template = inflater.inflate(R.layout.activity_samplecard, vlayout, false)

                        // 1. Get View References
                        val txtDate: TextView = template.findViewById(R.id.txtDateejbvj)
                        val imgMood: ImageView = template.findViewById(R.id.imgMoodejbvj)
                        val txtTitle: TextView = template.findViewById(R.id.txtTitleejbvj)
                        val txtWeather: TextView = template.findViewById(R.id.txtWeatherejbvj)
                        val txtLoc: TextView = template.findViewById(R.id.txtLocejbvj)
                        val txtTime: TextView = template.findViewById(R.id.txtTimeejbvj)
                        val btnDelete: ImageView = template.findViewById(R.id.btnDelete)

                        // 2. Extract Data (Safely handling nulls)
                        val docId = record.id
                        val title = record.getString("title") ?: "Untitled"
                        val content = record.getString("content") ?: ""
                        val location = record.getString("location") ?: ""
                        val dateEntry = record.getString("date") ?: ""
                        val timeEntry = record.getString("time") ?: ""
                        val weather = record.getString("weather") ?: ""
                        val weatherIcon = record.getString("weathericon") ?: "" // Ensure casing matches DB

                        // Note: Using lowercase as per your previous code snippet
                        val moodSrcName = record.getString("moodicon")
                        val moodColorHex = record.getString("moodcolor")
                        val moodText = record.getString("mood") ?: ""

                        // 3. Set Visuals
                        txtTitle.text = title
                        txtWeather.text = weather
                        txtLoc.text = location
                        txtDate.text = dateEntry
                        txtTime.text = timeEntry

                        btnDelete.setOnClickListener {
                            // 1. Create the Confirmation Dialog
                            android.app.AlertDialog.Builder(this)
                                .setTitle("Delete Entry")
                                .setMessage("Are you sure you want to delete this journal entry?")
                                .setPositiveButton("Yes") { _, _ ->
                                    // 2. User said Yes -> Delete from Firestore
                                    conn.collection("tbl_entries").document(docId).delete()
                                        .addOnSuccessListener {
                                            // 3. Remove the card from the screen immediately
                                            vlayout.removeView(template)
                                            Toast.makeText(this, "Entry Deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .setNegativeButton("No", null) // Do nothing on No
                                .show()
                        }

                        // 4. Handle Icon & Color Tint
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
                                } else {
                                    imgMood.clearColorFilter()
                                }
                            }
                        }

                        // 5. CLICK LISTENER -> Open EntryPage for Editing
                        template.setOnClickListener {
                            val intent = Intent(this, journalentry::class.java)

                            // Pass ID so entrypage knows this is an EDIT
                            intent.putExtra("entryid", docId)

                            // Pass Content for Editing
                            intent.putExtra("title", title)
                            intent.putExtra("content", content)
                            intent.putExtra("location", location)

                            // Pass Visual Context (So the top of entrypage looks right)
                            intent.putExtra("date", dateEntry)
                            intent.putExtra("time", timeEntry)
                            intent.putExtra("mood", moodText)
                            intent.putExtra("moodicon", moodSrcName)
                            intent.putExtra("moodcolor", moodColorHex)
                            intent.putExtra("weather", weather)
                            intent.putExtra("weathericon", weatherIcon)

                            startActivity(intent)
                        }

                        // 6. Add card to layout
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