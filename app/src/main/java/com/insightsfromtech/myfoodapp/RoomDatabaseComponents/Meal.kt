package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "meal_table", indices = [Index(value =["name","description"])])
data class Meal(
    @PrimaryKey (autoGenerate = true) val id : Int = 0,
    val dayOfWeek : String,
    val timeOfDay : String,
    val mealType: String,
    val name : String,
    val description : String,
    @DrawableRes val image : Int,
    var isChecked : Boolean = false,
    var isDefault : Boolean = false,
    var isSelected : Boolean = false,
    var isScanned : Boolean = false
)
