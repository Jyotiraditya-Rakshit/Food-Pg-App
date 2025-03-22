package com.insightsfromtech.myfoodapp.Activities

import android.animation.Animator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.insightsfromtech.myfoodapp.Fragments.HomeFragment
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDatabase
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.ScannedMeal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale


class ScannerActivity : AppCompatActivity() {
    val db by lazy { MealDatabase.getDatabase(this).taskDao()}
    private val daoScanned by lazy { MealDatabase.getDatabase(this).scannedMealDao() }
    private lateinit var textView: TextView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var button: FloatingActionButton
    private lateinit var materialCardView: MaterialCardView
    private lateinit var yourFood : TextView
    private lateinit var firebaseData : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanner)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        lottieAnimationView = findViewById(R.id.lottie_anime)
        textView = findViewById(R.id.text_view)
        materialCardView = findViewById(R.id.card_layout)
        button = findViewById(R.id.back_btn)
        yourFood = findViewById(R.id.your_food)

        firebaseData = Firebase.firestore

        // Initially hide text and button
        textView.visibility = View.INVISIBLE
        button.visibility = View.INVISIBLE

        // Retrieve the listener from HomeFragment (fixing previous incorrect initialization)

        if (getCurrentMealType() == null) {
            showAnimation(R.raw.cancel, "Sorry \n Not Meal Time")
        } else {
            processMealScan()
        }

        button.setOnClickListener {
            finish()
        }
    }

    private fun processMealScan() {
        val currentDate = LocalDate.now().toString()
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser?.uid


        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showAnimation(R.raw.correct,"Meal Scanned Successfully")
        val today = LocalDate.now()
        val currentTIme = LocalTime.now().hour.toString() + "h:" + LocalTime.now().minute.toString() + "m"
        val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        getUserPreferenceFromFirestore { meal ->
            meal?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val Meal = db.getSpecificMeal(dayOfWeek, getCurrentMealType()!! ,meal)
                    daoScanned.insertScannedMeal(ScannedMeal(foodImage = Meal.image, foodName = Meal.name, scanTime = currentTIme, Date = currentDate))
//                    val encodeImage = convertIntToBitmap(Meal.image)
//                    val userData = hashMapOf(
//                        "foodImage" to encodeImage,
//                        "foodName" to Meal.name,
//                        "scanTime" to currentTIme,
//                        "Date" to currentDate
//                    )
//                    firebaseData.collection("users").document(currentUser).collection("meal_Details").document(currentDate).set(userData)
                    withContext(Dispatchers.Main) {
                        findViewById<TextView>(R.id.food_title_scanner).text = Meal.name
                        findViewById<TextView>(R.id.food_description_scanner).text = Meal.description
                        findViewById<ImageView>(R.id.food_image_scanner).setImageResource(Meal.image)
                    }
                }
            } ?: run {
                Toast.makeText(this, "No meal preference found.", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun convertIntToBitmap(@DrawableRes image: Int) : String {
        val bitmapImage = BitmapFactory.decodeResource(this@ScannerActivity.resources, image)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }

    private fun getUserPreferenceFromFirestore(callback: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId!=null){
            firebaseData.collection("users").document(userId).collection("preferences").document("meal_preferences").get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val meal = document.getString("What type of food do you prefer?")
                        callback(meal)  // Pass result to callback
                    } else {
                        Toast.makeText(this, "Sorry, failed to fetch data", Toast.LENGTH_SHORT).show()
                        callback(null) // Pass null if data doesn't exist
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Firestore error!", Toast.LENGTH_SHORT).show()
                    callback(null) // Pass null on failure
                }
        }

    }

    private fun showAnimation(animationRes: Int, message: String) {
        lottieAnimationView.apply {
            setAnimation(animationRes)
            playAnimation()
            removeAllAnimatorListeners() // Remove old listeners to prevent multiple triggers
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    showTextAndButton(message)

                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }
    private fun getCurrentMealType(): String? {
        val currentTime = LocalTime.now()

        return when (currentTime.hour) {
            in 7..9 -> "Breakfast"  // Morning hours (6 AM to 10:59 AM)
            in 12..14 -> "Lunch"     // Midday hours (11 AM to 3:59 PM)
            in 19..21 -> "Dinner"    // Evening hours (6 PM to 10:59 PM)
            else -> "Lunch" // Any other time
        }
    }

    private fun showTextAndButton(message: String) {

        textView.text = message

        textView.apply {
            alpha = 0f
            translationY = 50f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }



        button.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
        if(getCurrentMealType() != null){
           yourFood.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(800)
            }
            materialCardView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }

    }
}

interface onScannerQr {
    fun getCurrentMealType(): String?
}
