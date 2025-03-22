package com.insightsfromtech.myfoodapp.Fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.MutableInt
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fashare.stack_layout.StackLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.insightsfromtech.myfoodapp.Activities.MainActivity
import com.insightsfromtech.myfoodapp.Activities.ScannerActivity
import com.insightsfromtech.myfoodapp.Activities.onScannerQr
import com.insightsfromtech.myfoodapp.Adapters.CardStackAdapter
import com.insightsfromtech.myfoodapp.Adapters.CarouselAdapter
import com.insightsfromtech.myfoodapp.Adapters.CustomStackLayout
import com.insightsfromtech.myfoodapp.DataClasses.CardDetails
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.mig35.carousellayoutmanager.CarouselLayoutManager
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.mig35.carousellayoutmanager.CenterScrollListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import android.util.Base64
import com.insightsfromtech.myfoodapp.Activities.UserProfile
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment(),onScannerQr,CustomStackLayout.onStackEmpty {
    lateinit var imageAdapter: CardStackAdapter
    lateinit var recyclerView : RecyclerView
    lateinit var imageList : List<Int>
    private var current_position  = 0
    lateinit var character_1 : ImageView
    lateinit var character_text : TextView
    var isStackEmpty : Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    lateinit var cardList : MutableList<CardDetails>
    private val mealDao by lazy{MealDatabase.getDatabase(requireContext()).taskDao()}
    private lateinit var userProfile : CircleImageView
    //private val intent by lazy { Intent(requireContext(),UserProfile::class.java) }

    private val scrollRunnable = object : Runnable {
        override fun run() {
            recyclerView.smoothScrollToPosition((current_position + 1) % imageList.size)
            current_position = (current_position + 1) % imageList.size
            handler.postDelayed(this, 3000)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        character_1 = view.findViewById(R.id.character)
        character_text = view.findViewById(R.id.character_text)
        recyclerView = view.findViewById(R.id.recycler_view)
        imageList = listOf(
            R.drawable.dall_e_2025_03_17_13_13_37___an_extended_version_of_the_second_image_from_a_vibrant__colorful_digital_illustration_banner_with_a_high_resolution_and_a_light_mint_green_background,
            R.drawable.scanner2
        )
        val intent = Intent(requireContext(),UserProfile::class.java)
        userProfile= view.findViewById(R.id.user_profile)
        setUserDetails { base64Image, fullName, email ->
            if (!base64Image.isNullOrEmpty()) {
                val bitmap = base64ToBitmap(base64Image)
                val compressedImage = compressBitmap(bitmap)
                userProfile.setImageBitmap(bitmap)
                intent.putExtra("userName",fullName)
                intent.putExtra("userEmail",email)
                intent.putExtra("userImage",compressedImage)
            } else {
                userProfile.setImageResource(R.drawable.pepperoni_pizza_beauty_1000x1000)
            }

        }

        userProfile.setOnClickListener {
            if (intent.hasExtra("userName") && intent.hasExtra("userEmail") && intent.hasExtra("userImage")) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Profile data is loading. Please wait!", Toast.LENGTH_SHORT).show()
            }
        }

        cardList = mutableListOf()


        val currentDate = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        if(currentDate == "Friday"){
            cardList.add(
                CardDetails("It's HouseKeeping Day","Make sure to get your room cleaned by today.",R.drawable.cleaning_tools,R.drawable.gradient_card_2)
            )

        }
        getUserPreferenceFromFirestore { meal->
            CoroutineScope(Dispatchers.IO).launch {
                val card = mealDao.getSpecificMeal(currentDate,getCurrentMealType() ?: "Dinner",meal!!)
                Log.e("CardDetails",card.toString())
                cardList.add(
                    CardDetails("Today's Meal",card.description,R.drawable.output_onlinepngtools__2_,R.drawable.gradient_card)
                )
                withContext(Dispatchers.Main){
                    val stackLayout = view.findViewById<CustomStackLayout>(R.id.stack_layout)
                    stackLayout.onStackEmptyListener = this@HomeFragment
                    imageAdapter = CardStackAdapter(cardList.toMutableList(), stackLayout)

                    stackLayout.setAdapter(imageAdapter)
                }
            }
        }







        setupRecyclerView()
        val qrCodeBtn = requireActivity().findViewById<FloatingActionButton>(R.id.qr_code_btn)
        qrCodeBtn.setOnClickListener{
            scanQrCode()
        }

        if(isStackEmpty){
            characterShowcase()
        }else{
            character_1.visibility = View.GONE
            character_text.visibility = View.GONE
        }


    }

    fun compressBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream) // Reduce quality to 50%
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private fun setupRecyclerView() {
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = CarouselAdapter(imageList)
        recyclerView.addOnScrollListener(CenterScrollListener())
    }


    private fun setUserDetails(callback: (String, String?,String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = Firebase.firestore

        if (userId != null) {
            db.collection("users").document(userId).collection("userDetails").document("details")
                .get()
                .addOnSuccessListener {document->
                    if (document.exists()) {
                        val base64Image = document.getString("imageBase64") ?: "lol"
                        val fullName = document.getString("fullName") ?: "Unknown User"
                        val userEmail = document.getString("email") ?: "No Email"
                        callback(base64Image,fullName,userEmail)
                    }
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Failed to fetch user data", it)
                    Toast.makeText(requireContext(), "Failed to load profile details", Toast.LENGTH_SHORT).show()
                }
        }
    }




    private fun scanQrCode() {
        val options = ScanOptions()

        options.setPrompt("Scan the QR Code")
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setOrientationLocked(true) // Keep it locked
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)

        qrScannerLauncher.launch(options)

    }

    private val qrScannerLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            validateContent(result.contents)
        }
    }

    private fun validateContent(scannedData : String) {

        if(scannedData == "UNIVERSAL_MEAL_QR"){
            val intent = Intent(requireContext() , ScannerActivity::class.java)
            startActivity(intent)
        }
        else Toast.makeText(requireContext(),"Invalid QR Code", Toast.LENGTH_SHORT).show()


    }
    override fun getCurrentMealType(): String? {
        val currentTime = LocalTime.now()

        return when (currentTime.hour) {
            in 7..9 -> "Breakfast"  // Morning hours (6 AM to 10:59 AM)
            in 12..14 -> "Lunch"     // Midday hours (11 AM to 3:59 PM)
            in 19..21 -> "Dinner"    // Evening hours (6 PM to 10:59 PM)
            else -> null // Any other time
        }
    }

    override fun characterShowcase() {
        character_1.visibility = View.VISIBLE
        character_text.visibility = View.VISIBLE
        isStackEmpty = true
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(scrollRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(scrollRunnable)
    }

    private fun getUserPreferenceFromFirestore(callback: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseData = Firebase.firestore

        if (userId != null) {
            firebaseData.collection("users").document(userId).collection("preferences").document("meal_preferences").get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val meal = document.getString("What type of food do you prefer?")
                        callback(meal)  // Pass result to callback
                    } else {
                        Toast.makeText(requireContext(), "Sorry, failed to fetch data", Toast.LENGTH_SHORT).show()
                        callback(null) // Pass null if data doesn't exist
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Firestore error!", Toast.LENGTH_SHORT).show()
                    callback(null) // Pass null on failure
                }
        }
    }
}