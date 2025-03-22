package com.insightsfromtech.myfoodapp.Activities.LoginPages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.insightsfromtech.myfoodapp.Activities.MainActivity
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.databinding.ActivitySigninBinding

class SigningActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        window.statusBarColor = Color.TRANSPARENT

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                signInUser(email, password)
            }
        }

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.error = "Email is required"
            return false
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "Password is required"
            return false
        }
        return true
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Navigate to MainActivity or Home Screen
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show()
                }
            }
    }

//    private fun signOutUser() {
//        // Sign out from Firebase
//        FirebaseAuth.getInstance().signOut()
//
//        // Show a toast message indicating sign out
//        Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show()
//
//        // Redirect the user to SignInActivity
//        val intent = Intent(this, SignInActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
//        startActivity(intent)
//        finish() // Close the current activity
//    }
}
