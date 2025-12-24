package com.example.projectappdev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val txtName: TextView = findViewById(R.id.txtName)
        val txtEmail: TextView = findViewById(R.id.txtEmail)
        val btnLogout: Button = findViewById(R.id.btnLogout)
        val imgBack2: ImageView = findViewById(R.id.img_goback2)

        val auth = FirebaseAuth.getInstance()
        val con = FirebaseFirestore.getInstance()

        val name = auth.currentUser!!.displayName
        val email = auth.currentUser!!.email
        val userid = auth.currentUser!!.uid

        val btnHome: ImageView = findViewById(R.id.btnHome1)
        val btnAdd: ImageView = findViewById(R.id.btnAdd1)
        val btnAnalytics: ImageView = findViewById(R.id.btnAnalytics1)

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

        con.collection("tbl_users").document(userid).get()
            .addOnSuccessListener { rec ->
                val c_name = rec.getString("name")
                txtName.text = name ?: c_name ?: "No name"
                txtEmail.text = email
            }

        imgBack2.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            auth.signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            GoogleSignIn.getClient(this, gso).signOut()

            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }
    }
}