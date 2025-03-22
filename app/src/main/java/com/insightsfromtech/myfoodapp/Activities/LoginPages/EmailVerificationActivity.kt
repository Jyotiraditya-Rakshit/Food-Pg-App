package com.insightsfromtech.myfoodapp.Activities.LoginPages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        window.statusBarColor = Color.TRANSPARENT

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink = pendingDynamicLinkData?.link

                if (deepLink != null && deepLink.getBooleanQueryParameter("verify", false)) {
                    verifyEmail()
                } else {
                    Toast.makeText(this, "Invalid verification link.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener(this) {
                Toast.makeText(this, "Failed to retrieve dynamic link.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verifyEmail() {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener {
                if (user.isEmailVerified) {
                    Toast.makeText(this, "Email successfully verified!", Toast.LENGTH_LONG).show()
                    // Redirect to SignInActivity
                    startActivity(Intent(this, SigningActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Email not verified yet. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No user logged in. Please register or sign in.", Toast.LENGTH_SHORT).show()
        }
    }
}
