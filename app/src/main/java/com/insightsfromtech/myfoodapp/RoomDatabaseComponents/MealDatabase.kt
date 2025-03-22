package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Meal::class , ScannedMeal::class], version = 2, exportSchema = false)
abstract class MealDatabase : RoomDatabase() {

    abstract fun taskDao(): MealDao
    abstract fun scannedMealDao() : ScannedMealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val isFirstRun = prefs.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                context.deleteDatabase("meal-db") // Delete Room database on first launch
                prefs.edit().putBoolean("isFirstRun", false).apply()
            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use applicationContext to avoid memory leaks
                    MealDatabase::class.java,
                    "meal-db"
                ).fallbackToDestructiveMigration() // Ensures DB resets on schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
