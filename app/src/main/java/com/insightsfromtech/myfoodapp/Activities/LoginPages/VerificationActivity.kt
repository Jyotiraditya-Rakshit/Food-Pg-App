package com.insightsfromtech.myfoodapp.Activities.LoginPages

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.insightsfromtech.myfoodapp.R
import org.w3c.dom.Text

class VerificationActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: LottieAnimationView
    private lateinit var tickAnimation: LottieAnimationView
    private lateinit var signInButton: MaterialButton
    private lateinit var verificationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification2)

        window.statusBarColor = Color.TRANSPARENT

        verificationText = findViewById(R.id.verification_text)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.verification)
        tickAnimation = findViewById(R.id.lottie_view)
        signInButton = findViewById(R.id.btn)

        signInButton.setOnClickListener {
            // Redirect to Sign-In Activity
            startActivity(Intent(this, PreferencesActivity::class.java))
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        checkEmailVerification()
    }

    override fun onPause() {
        super.onPause()
        progressBar.visibility = View.GONE
        verificationText.visibility = View.GONE

    }

    private fun checkEmailVerification() {
        val user = auth.currentUser
        if (user != null) {

            // Reload user to fetch updated verification status
            user.reload().addOnCompleteListener { reloadTask ->

                if (reloadTask.isSuccessful && user.isEmailVerified) {
                    // Email is verified
                    tickAnimation.visibility = View.VISIBLE
                    tickAnimation.playAnimation()

                    // Show "Sign In" button after animation ends
                    tickAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            signInButton.visibility = View.VISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                            TODO("Not yet implemented")
                        }

                    })
                } else {
                    // Email not verified yet
                    progressBar.visibility = View.VISIBLE
                    verificationText.visibility = View.VISIBLE
                    Toast.makeText(this, "Please verify your email and return to the app.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}