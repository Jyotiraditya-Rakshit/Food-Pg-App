package com.insightsfromtech.myfoodapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.insightsfromtech.myfoodapp.R

class CarouselAdapter(@DrawableRes private val listOfImages : List<Int>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {


    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView = itemView.findViewById<ImageView>(R.id.image_view)

        fun bind(img : Int){
            imageView.setImageResource(img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfImages.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(listOfImages[position])
    }
}