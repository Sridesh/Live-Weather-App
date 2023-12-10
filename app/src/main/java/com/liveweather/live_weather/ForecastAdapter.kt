package com.liveweather.live_weather
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ForecastAdapter(private val forecastList: List<ForecastEntry>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val entry = forecastList[position]
        holder.bind(entry)
        Log.d("ForecastAdapter", "Binding item at position $position")
    }

    override fun getItemCount(): Int = forecastList.size

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        private val textTemperature: TextView = itemView.findViewById(R.id.textTemperature)
        private val textHumidity: TextView = itemView.findViewById(R.id.textHumidity)
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val img: ImageView = itemView.findViewById(R.id.img)
        private val textMainn: TextView = itemView.findViewById(R.id.des)

        fun bind(entry: ForecastEntry) {
            textDate.text= "${entry.dates}"
            textTime.text = "${entry.time}"
            textTemperature.text = "${entry.temperature}°C"
            textHumidity.text = "${entry.humidity}"
            textMainn.text = "${entry.mainn}"

            var iconUrl = "https://openweathermap.org/img/wn/${entry.icon}@2x.png"
            Picasso.get().load(iconUrl).into(img)


        }
    }
}