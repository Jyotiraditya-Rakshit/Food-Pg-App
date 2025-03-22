package com.insightsfromtech.myfoodapp.RoomDatabaseComponents

import androidx.annotation.DrawableRes
import androidx.room.PrimaryKey

data class Cards(
    @PrimaryKey (autoGenerate = true) val id : Int  = 0 ,
    @DrawableRes val cardImage : Int,
    val cardDescription : String,
)
