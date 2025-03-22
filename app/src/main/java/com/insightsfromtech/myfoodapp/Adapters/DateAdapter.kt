import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insightsfromtech.myfoodapp.DataClasses.DateCard
import com.insightsfromtech.myfoodapp.R

class DateAdapter(
    private val dateCardList: List<DateCard>,
    private val listener: onButtonClicked
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    interface onButtonClicked {
        fun onClikced(day: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.days_calender, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dateCardList[position])
    }

    override fun getItemCount(): Int = dateCardList.size

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val day: TextView = itemView.findViewById(R.id.day)
        private val date: TextView = itemView.findViewById(R.id.date_for_calender)
        val cardLayout = itemView.findViewById<LinearLayout>(R.id.card_layout)

        fun bind(item: DateCard) {
            day.text = item.day
            date.text = "${item.date} ${item.month}"
            listener.onClikced(day.text.toString())

            if (item.isSelected) {
                cardLayout.setBackgroundColor(Color.GREEN)
                day.setTextColor(Color.WHITE)
                date.setTextColor(Color.WHITE)
            } else {
                cardLayout.setBackgroundColor(Color.WHITE)
                day.setTextColor(Color.BLACK)
                date.setTextColor(Color.BLACK)
            }

            itemView.setOnClickListener {
                val previousSelectedIndex = dateCardList.indexOfFirst { it.isSelected }
                if (previousSelectedIndex != -1) {
                    dateCardList[previousSelectedIndex].isSelected = false
                    notifyItemChanged(previousSelectedIndex)
                }
                item.isSelected = true
                notifyItemChanged(adapterPosition)
                 // Pass String to callback
            }
        }
    }
}

