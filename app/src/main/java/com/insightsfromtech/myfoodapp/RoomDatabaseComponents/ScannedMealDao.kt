package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScannedMealDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertScannedMeal(scannedMeal: ScannedMeal)

    @Query("SELECT * FROM scanned_meal_table ")
    fun getAllScannedMeals(): List<ScannedMeal>

}