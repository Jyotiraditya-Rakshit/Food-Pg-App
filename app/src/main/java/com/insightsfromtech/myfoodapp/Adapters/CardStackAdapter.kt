package com.insightsfromtech.myfoodapp.Adapters

import android.content.Context
import android.util.MutableInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton
import com.insightsfromtech.myfoodapp.DataClasses.CardDetails
import com.insightsfromtech.myfoodapp.R

class CardStackAdapter(
    private val cardList: MutableList<CardDetails>,
    private val stackLayout: CustomStackLayout
) : StackAdapter() {

    override fun getItemCount(): Int = cardList.size

    override fun createView(parent: ViewGroup, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_cards, parent, false)
        val title: TextView = view.findViewById(R.id.title_card)
        val desc: TextView = view.findViewById(R.id.description_food)
        val image: ImageView = view.findViewById(R.id.card_logo)
        val layoutCard : ConstraintLayout = view.findViewById(R.id.main_card_layout)
        val btnRemove: MaterialButton = view.findViewById(R.id.donebtn)

        val card = cardList[position]
        title.text = card.cardTitle
        desc.text = card.cardDescription
        layoutCard.setBackgroundResource(card.gradientRes)
        image.setImageResource(card.cardImage)

        btnRemove.setOnClickListener {
            stackLayout.removeTopCard()
        }

        return view
    }

    override fun onCardRemoved(position: Int) {
        if (position < cardList.size) {
            cardList.removeAt(position)
        }
    }

}
