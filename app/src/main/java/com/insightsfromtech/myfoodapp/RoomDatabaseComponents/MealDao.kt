package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MealDao {

    @Query("SELECT  * FROM meal_table WHERE dayOfWeek = :day AND timeOfDay = :time LIMIT 2")
    fun getMealByDayAndTime(day : String , time : String): List<Meal>

    @Query("SELECT * FROM meal_table WHERE dayOfWeek = :day AND timeOfDay = :time AND mealType = :preference")
    fun getSpecificMeal(day : String , time : String , preference : String) : Meal

    @Query("SELECT * FROM meal_table WHERE isScanned = 1")
    fun getTheScannedMeal() : List<Meal>

    @Update
    fun updateMeal(mealItem : Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeals(mealItem: List<Meal>)


}