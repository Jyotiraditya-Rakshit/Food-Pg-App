import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insightsfromtech.myfoodapp.R
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.Meal
import com.insightsfromtech.myfoodapp.RoomDatabaseComponents.ScannedMeal
import de.hdodenhof.circleimageview.CircleImageView


class HistoryAdapter :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items: List<ScannedMeal> = emptyList()


    fun setItems(newItems: List<ScannedMeal>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_layout, parent, false)
        return HistoryViewHolder(view)
    }


    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }


    override fun getItemCount(): Int = items.size


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val foodImage: CircleImageView? = itemView.findViewById(R.id.food_image)
        val foodTitle: TextView? = itemView.findViewById(R.id.food_name)
        val scanTime: TextView? = itemView.findViewById(R.id.scan_datetime)
        val date: TextView? = itemView.findViewById(R.id.date) // Make sure it's lowercase

        init {
            Log.d("HistoryViewHolder", "foodImage: $foodImage")
            Log.d("HistoryViewHolder", "foodTitle: $foodTitle")
            Log.d("HistoryViewHolder", "scanTime: $scanTime")
            Log.d("HistoryViewHolder", "date: $date")
        }




        fun bind(item: ScannedMeal) {


            foodImage?.setImageResource(item.foodImage)
            foodTitle?.text = item.foodName
            scanTime?.text = item.scanTime
            date?.text = item.Date


        }
    }
}