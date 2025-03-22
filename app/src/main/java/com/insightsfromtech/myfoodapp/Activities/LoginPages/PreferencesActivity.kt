package com.insightsfromtech.myfoodapp.Activities.LoginPages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.insightsfromtech.myfoodapp.Activities.MainActivity
import com.insightsfromtech.myfoodapp.R

class PreferencesActivity : AppCompatActivity() {
    private lateinit var questionText: TextView
    private lateinit var optionGroup: RadioGroup
    private lateinit var nextButton: MaterialButton

    private var currentQuestionIndex = 0
    private val preferences = mutableMapOf<String, String>() // Store temp user preferences

    private val questions = listOf(
        "What type of food do you prefer?" to listOf("Veg", "Non-Veg"),
        "What type of beverages do you like?" to listOf("Tea", "Coffee", "Juices")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        questionText = findViewById(R.id.questionText)
        optionGroup = findViewById(R.id.optionGroup)
        nextButton = findViewById(R.id.nextButton)

        loadQuestion()


        window.statusBarColor = Color.TRANSPARENT

        nextButton.setOnClickListener {
            val selectedOptionId = optionGroup.checkedRadioButtonId
            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save preference
            val selectedOption = findViewById<RadioButton>(selectedOptionId).text.toString()
            preferences[questions[currentQuestionIndex].first] = selectedOption

            // Move to the next question
            currentQuestionIndex++

            if (currentQuestionIndex < questions.size) {
                loadQuestion()
            } else {
                // Save preferences to Firebase
                savePreferencesToFirebase(preferences)
                // Redirect to Home Page
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun loadQuestion() {
        val currentQuestion = questions[currentQuestionIndex]
        questionText.text = currentQuestion.first
        optionGroup.removeAllViews()

        currentQuestion.second.forEach { option ->
            val radioButton = RadioButton(this)
            radioButton.text = option
            optionGroup.addView(radioButton)
        }

        // Change button text to "Done" for the last question
        nextButton.text = if (currentQuestionIndex == questions.size - 1) "Done" else "Next"

        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        questionText.startAnimation(fadeOut)
        optionGroup.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Update the content after fade-out
                loadQuestion() // Update question and options

                // Fade in the new content
                val fadeIn = AnimationUtils.loadAnimation(this@PreferencesActivity, R.anim.fade_in)
                questionText.startAnimation(fadeIn)
                optionGroup.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun savePreferencesToFirebase(preferences: Map<String, String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).collection("preferences").document("meal_preferences")
            .set(preferences)
            .addOnSuccessListener {
                Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save preferences.", Toast.LENGTH_SHORT).show()
            }
    }
}