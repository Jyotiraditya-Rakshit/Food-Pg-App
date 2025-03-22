package com.insightsfromtech.myfoodapp.DataClasses

import android.graphics.Bitmap
import androidx.annotation.DrawableRes

data class MealHistory(
    var imageResId: Bitmap,
    var name: String="",
    var scanTime: String="",
    var scanDate: String = ""
)
