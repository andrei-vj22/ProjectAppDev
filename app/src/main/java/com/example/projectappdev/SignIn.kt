package com.example.projectappdev

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtEmail: EditText = findViewById(R.id.edt_SIemail)
        val edtPass: EditText = findViewById(R.id.edt_SIpass)
        val btnLog: Button = findViewById(R.id.btn_signin)
        val btnGoogle: Button = findViewById(R.id.btn_google)
        val noAcc: TextView = findViewById(R.id.txt_noAcc)

        val auth = FirebaseAuth.getInstance()
        val con = FirebaseFirestore.getInstance()

        // Google Sign-In Setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, gso)

        // Google Result Launcher
        val googleLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential).addOnSuccessListener {
                    val user = auth.currentUser
                    val userid = user?.uid ?: ""
                    val email = user?.email ?: ""
                    val name = user?.displayName ?: "Anonymous"

                    val values = mapOf(
                        "name" to name,
                        "email" to email,
                    )

                    con.collection("tbl_users").document(userid).set(values)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Welcome $name", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Home::class.java))
                            finish() // Prevent going back to login
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Firebase Auth Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Google Login Cancelled/Failed", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogle.setOnClickListener {
            googleLauncher.launch(googleClient.signInIntent)
        }

        noAcc.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        // --- FIXED LOGIN BUTTON WITH VALIDATION ---
        btnLog.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            // 1. Check if fields are empty
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
            // 2. Optional: Basic email format check
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
            else {
                // 3. Attempt Sign In
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Log In successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Catching errors like "Wrong Password" or "No User Found"
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}