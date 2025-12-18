package com.example.projectappdev

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val SignUpB : Button = findViewById(R.id.btn_signup)
        val pass: EditText = findViewById(R.id.edt_pass)
        val confPass: EditText = findViewById(R.id.edt_confPass)
        val edtName: EditText = findViewById(R.id.edt_name)
        val edtEmail: EditText = findViewById(R.id.edt_email)
        val logIn: TextView = findViewById(R.id.txt_already)
        val imgBack: ImageView = findViewById(R.id.img_goback)

        val con = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        imgBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        logIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        SignUpB.setOnClickListener {
            val edtName = edtName.text.toString()
            val edtEmail = edtEmail.text.toString()
            val pass = pass.text.toString()
            val confPass = confPass.text.toString()

            if (edtName.isEmpty() || edtEmail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 6) {

                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            } else if (pass != confPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {

                auth.createUserWithEmailAndPassword(edtEmail, pass)
                    .addOnSuccessListener {
                        val userid = auth.currentUser?.uid ?: ""

                        val values = mapOf(
                            "name" to edtName,
                            "email" to edtEmail,
                        )

                        con.collection("tbl_users").document(userid).set(values)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, SignIn::class.java)
                                startActivity(intent)
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Sign up failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }


        }}}
