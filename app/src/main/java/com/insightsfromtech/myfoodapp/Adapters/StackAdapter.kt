package com.insightsfromtech.myfoodapp.Adapters

import android.view.View
import android.view.ViewGroup

abstract class StackAdapter {
    abstract fun getItemCount(): Int
    abstract fun createView(parent: ViewGroup, position: Int): View
    abstract fun onCardRemoved(position: Int)

}