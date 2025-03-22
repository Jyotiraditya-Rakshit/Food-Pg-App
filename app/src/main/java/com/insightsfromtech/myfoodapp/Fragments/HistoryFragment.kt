package com.insightsfromtech.myfoodapp.Fragments

import HistoryAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.Query
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mealHistoryAdapter: HistoryAdapter
    private var mealHistoryList = emptyList<Meal>()
    private val dao by lazy { MealDatabase.getDatabase(requireContext()).taskDao() }
    private val daoScanned by lazy { MealDatabase.getDatabase(requireContext()).scannedMealDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val items = listOf("Recent","Oldest")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,items)
        view.findViewById<Spinner>(R.id.spinner).adapter = adapter



        recyclerView = view.findViewById(R.id.recyclerView)
        mealHistoryAdapter = HistoryAdapter()
        recyclerView.adapter = mealHistoryAdapter

        view.findViewById<Spinner>(R.id.spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long){
                val spinnerDetails = if(position == 0) Query.Direction.DESCENDING else Query.Direction.ASCENDING
                getHistory(spinnerDetails)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                getHistory(Query.Direction.DESCENDING)
            }
        }




        val history_three_dots = view.findViewById<ImageButton>(R.id.SheetDialog)




        history_three_dots.setOnClickListener {
            val view1 = layoutInflater.inflate(R.layout.bottom_sheet_dialog , null)
            getUserPreferenceFromFirestore({
                    meal ->
                view1.findViewById<TextView>(R.id.diet_type).text = meal
            },{
                    meal2 ->
                view1.findViewById<TextView>(R.id.beverage).text = meal2
            })
            val downArrow = view1.findViewById<ImageButton>(R.id.down_arrow)
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(view1)
            dialog.show()

            downArrow.setOnClickListener {
                dialog.dismiss()
            }
        }






    }

    override fun onResume() {
        super.onResume()
        getHistory(Query.Direction.DESCENDING)
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


    private fun getUserPreferenceFromFirestore(callback: (String?) -> Unit , callback1: (String?) -> Unit) {
        val db = Firebase.firestore
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if(userID!=null){
            db.collection("users").document(userID).collection("preferences").document("meal_preferences").get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val meal = document.getString("What type of food do you prefer?")
                        val meal1 = document.getString("What type of beverages do you like?")
                        callback(meal)  // Pass result to callback
                        callback1(meal1)
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

    fun getHistory(sortOrder: Query.Direction) {
        CoroutineScope(Dispatchers.IO).launch {
            var mealHistory = daoScanned.getAllScannedMeals()

            // Apply sorting based on user selection
            mealHistory = if (sortOrder == Query.Direction.DESCENDING) {
                mealHistory.sortedByDescending { it.Date } // Replace `date` with your sorting field
            } else {
                mealHistory.sortedBy { it.Date }
            }

            withContext(Dispatchers.Main) {
                mealHistoryAdapter.setItems(mealHistory)
                mealHistoryAdapter.notifyDataSetChanged()

                view?.findViewById<LottieAnimationView>(R.id.search_bar)?.visibility = if (mealHistory.isNotEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

}

