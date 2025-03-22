package com.insightsfromtech.myfoodapp.Adapters
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal



class MealAdapter(var listOfMeals : List<Meal>,val listener : saveButtonClick) : RecyclerView.Adapter<MealAdapter.ViewHolder>() {

    interface saveButtonClick{
        fun onClick(meal : Meal)
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val titleFood : TextView = itemView.findViewById(R.id.food_title)
        val description : TextView = itemView.findViewById(R.id.food_description)
        val materialButton : MaterialRadioButton = itemView.findViewById(R.id.radio_button)
        val foodImage : ImageView = itemView.findViewById(R.id.food_image_layout)
        val layout : ConstraintLayout = itemView.findViewById(R.id.menu_layout)
        fun bind(item : Meal){
            titleFood.text = item.name
            description.text = item.description
            materialButton.isChecked = item.isSelected

            foodImage.setImageResource(item.image)


            itemView.setOnClickListener  {
                // Deselect all radio buttons first
                listOfMeals.forEach { meal ->
                    meal.isSelected = false
                }

                item.isSelected = true
                listener.onClick(item)
                notifyDataSetChanged()
            }

            materialButton.setOnClickListener  {
                // Deselect all radio buttons first
                listOfMeals.forEach { meal ->
                    meal.isSelected = false
                }

                item.isSelected = true
                listener.onClick(item)
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.food_layouts,parent,false))
    }

    override fun getItemCount(): Int {
        return listOfMeals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfMeals[position])
    }

}