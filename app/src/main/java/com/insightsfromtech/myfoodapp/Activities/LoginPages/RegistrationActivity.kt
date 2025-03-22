package com.insightsfromtech.myfoodapp.Activities.LoginPages

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.databinding.ActivityRegistrationBinding
import java.io.ByteArrayOutputStream

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding1: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedBitmap: Bitmap? = null  // Store selected image as Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1 = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding1.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        window.statusBarColor = Color.TRANSPARENT

        // Open Image Picker when user clicks on image
        binding1.userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        // Handle Registration
        binding1.signUpButton.setOnClickListener {
            val fullName = binding1.fullNameEditText.text.toString().trim()
            val email = binding1.emailEditText.text.toString().trim()
            val password = binding1.passwordEditText.text.toString().trim()
            val confirmPassword = binding1.confirmPasswordEditText.text.toString().trim()

            if (selectedBitmap == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validateInput(fullName, email, password, confirmPassword)) {
                registerUser(email, password, fullName)
            }
        }

        binding1.signInText.setOnClickListener {
            finish()
        }
    }

    // Improved method to handle image selection
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                selectedBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))
                binding1.userImage.setImageBitmap(selectedBitmap)
            }
        }

    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        when {
            fullName.isEmpty() -> {
                binding1.fullNameEditText.error = "Full Name is required"
                return false
            }
            email.isEmpty() -> {
                binding1.emailEditText.error = "Email is required"
                return false
            }
            password.isEmpty() -> {
                binding1.passwordEditText.error = "Password is required"
                return false
            }
            password.length < 6 -> {
                binding1.passwordEditText.error = "Password must be at least 6 characters"
                return false
            }
            password != confirmPassword -> {
                binding1.confirmPasswordEditText.error = "Passwords do not match"
                return false
            }
            else -> return true
        }
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val encodedImage = selectedBitmap?.let { bitmapToBase64(it) }
                    saveUserData(fullName, email, encodedImage)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserData(fullName: String, email: String, encodedImage: String?) {
        val userId = auth.currentUser?.uid ?: return
        val userMap = hashMapOf(
            "uid" to userId,
            "fullName" to fullName,
            "email" to email,
            "imageBase64" to encodedImage  // Store Base64 image in Firestore
        )

        db.collection("users").document(userId).collection("userDetails").document("details")
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                sendEmailVerification()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { verifyTask ->
                if (verifyTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Registration successful. Please verify your email before signing in.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@RegistrationActivity, VerificationActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Convert Bitmap to Base64 string
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Convert Base64 string to Bitmap
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    // Function to retrieve and display the stored image
    private fun retrieveUserImage(userId: String) {
        db.collection("users").document(userId).collection("userDetails").document("details")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val base64String = document.getString("imageBase64")
                    if (!base64String.isNullOrEmpty()) {
                        val bitmap = base64ToBitmap(base64String)
                        binding1.userImage.setImageBitmap(bitmap)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
            }
    }
}
