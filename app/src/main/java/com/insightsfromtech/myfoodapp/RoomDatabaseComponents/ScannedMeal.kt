package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.protobuf.Value


@Entity(tableName = "scanned_meal_table" , indices = [Index(value = ["foodName"], unique = true)])
data class ScannedMeal(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @DrawableRes val foodImage : Int,
    val foodName : String,
    val scanTime : String,
    val Date : String
)
