package com.insightsfromtech.myfoodapp.DataClasses

data class DateCard(
    val date : String,
    val month : String,
    val day : String,
    var isSelected : Boolean = false
)
