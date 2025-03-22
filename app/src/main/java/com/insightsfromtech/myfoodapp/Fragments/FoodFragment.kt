package com.insightsfromtech.myfoodapp.Fragments

import DateAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.firestore
import com.insightsfromtech.myfoodapp.Adapters.MealAdapter
import com.insightsfromtech.myfoodapp.DataClasses.DateCard
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDao
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.MealDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale


class FoodFragment : Fragment(),DateAdapter.onButtonClicked,MealAdapter.saveButtonClick{
    private lateinit var mealDao : MealDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingFood : LottieAnimationView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val db = MealDatabase.getDatabase(requireContext())
        mealDao = db.taskDao()
        recyclerView = view.findViewById(R.id.food_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val recyclerView2 = view.findViewById<RecyclerView>(R.id.week_calender)
        recyclerView2.adapter = DateAdapter(generateCardList(),this)
        val mealText = view.findViewById<TextView>(R.id.preparing_meal)
        val liveText = view.findViewById<TextView>(R.id.live_text)
        mealText.text = getMealType()
        liveText.text = getLiveData()
        loadingFood = requireActivity().findViewById(R.id.loading_food)
        loadingFood.visibility = View.VISIBLE

        Log.d("CardsList", "List : ${generateCardList()}")






    }




    private fun getUserPreferenceFromFirestore(callback: (String?) -> Unit ) {
        val db = Firebase.firestore
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            db.collection("users").document(userID).collection("preferences").document("meal_preferences").get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val meal = document.getString("What type of food do you prefer?")
                        val meal1 = document.getString("What type of beverages do you like?")
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



    fun rearrangeMealsBasedOnPreference(meals: List<Meal>, preference: String?): List<Meal> {
        return meals.sortedWith(compareByDescending { it.mealType == preference })
    }

    fun getMealsForDayAndTimeWithPreference(
        mealDao: MealDao,
        day: String,
        time: String,
        preference: String?, // "Veg" or "Non-Veg"
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val meals = mealDao.getMealByDayAndTime(day,time)
            val rearrangedMeals = rearrangeMealsBasedOnPreference(meals, preference)

            withContext(Dispatchers.Main) {
                updateList(rearrangedMeals)
                loadingFood.visibility = View.GONE
            }
        }
    }
    private fun generateCardList() : List<DateCard>{

        val dateCards = mutableListOf<DateCard>()
        val today = LocalDate.now()
        for (i in 0..6) {
            val currentDate = today.plusDays(i.toLong())
            val dayName = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val dateString = currentDate.dayOfMonth.toString()
            val monthName = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())


            dateCards.add(
                DateCard(
                    day = dayName,
                    month = monthName,
                    date = dateString,
                )
            )
        }
        return dateCards
    }


    fun updateList(listOfMeals: List<Meal>) {
        (recyclerView.adapter as? MealAdapter)?.apply {
            this.listOfMeals = listOfMeals
            notifyDataSetChanged()
        } ?: run {
            val mealAdapter = MealAdapter(listOfMeals,this)
            recyclerView.adapter = mealAdapter
        }
    }

    override fun onClikced(day: String) {
        getUserPreferenceFromFirestore { userPreference ->
            getMealsForDayAndTimeWithPreference(mealDao, day, "Breakfast", userPreference)

            view?.findViewById<MaterialButton>(R.id.breakfast)?.setOnClickListener {
                getMealsForDayAndTimeWithPreference(mealDao, day, "Breakfast", userPreference)
            }

            view?.findViewById<MaterialButton>(R.id.lunch)?.setOnClickListener {
                getMealsForDayAndTimeWithPreference(mealDao, day, "Lunch", userPreference)
            }

            view?.findViewById<MaterialButton>(R.id.dinner)?.setOnClickListener {
                getMealsForDayAndTimeWithPreference(mealDao, day, "Dinner", userPreference)
            }
        }
    }


    fun getLiveData(): String {
        val time = LocalTime.now()
        return when (time.hour) {
            in 22..23, in 0..6 -> "Live from 7:30 am"
            in 9..11 -> "Live from 11:30 am"
            in 14..18 -> "Live from 7:30 pm"
            else -> "Enjoy your Meal"
        }
    }


    fun getMealType(): String {
        val currentTime = LocalTime.now()

        return when (currentTime.hour) {
            in 7..9 -> "Breakfast is Live"  // Morning hours (6 AM to 10:59 AM)
            in 12..14 -> "Lunch is Live"     // Midday hours (11 AM to 3:59 PM)
            in 19..21 -> "Dinner is Live"    // Evening hours (6 PM to 10:59 PM)
            else -> "Preparing your meal"   // Any other time
        }
    }

    override fun onClick(meal: Meal) {
        view?.findViewById<MaterialButton>(R.id.save)?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mealDao.updateMeal(meal.copy(isChecked = true, isDefault = true))
                withContext(Dispatchers.Main) {
                    Log.d("message","Saved Succesfully")
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
