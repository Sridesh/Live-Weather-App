package com.liveweather.live_weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import android.content.Intent
import android.widget.TextView
import com.liveweather.live_weather.ForecastAdapter
import android.util.Log


class Forecast : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    //    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var forecastData: List<ForecastEntry>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)


        val intent: Intent = intent
        val lon: String? = intent.getStringExtra("key1")
        val lat: String? = intent.getStringExtra("key2")



        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=bc7b5d35939d2152c5319a7c5247387d&units=metric"

        Thread {
            forecastData = fetchData(apiUrl).flatMap { it.entries }
            runOnUiThread {
                val forecastAdapter = ForecastAdapter(forecastData)
                recyclerView.adapter = forecastAdapter
            }
        }.start()
    }

    private fun fetchData(apiUrl: String): List<ForecastDay> {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Store the list of ForecastDay objects in a variable
            val forecastData = parseForecastData(JSONObject(response.toString()))

            return forecastData
        } finally {
            connection.disconnect()
        }
    }

    private fun parseForecastData(json: JSONObject): List<ForecastDay> {
        val list = json.getJSONArray("list")
        val forecastData = mutableListOf<ForecastDay>()

        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)

            val timestamp = item.getLong("dt") * 1000 // Convert to milliseconds
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateString = dateFormat.format(date)

            val temperature = item.getJSONObject("main").getDouble("temp")
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            val humidity = item.getJSONObject("main").getInt("humidity")
            val dates = dateString
            val weatherArray = item.getJSONArray("weather")
            lateinit var icon: String
            lateinit var mainn: String

            if (weatherArray.length() > 0) {
                val weatherObject = weatherArray.getJSONObject(0)
                icon = weatherObject.getString("icon")
                mainn = weatherObject.getString("main")
            }


            Log.d("Forecast", "Processing item $i")
            Log.d("Forecast", "dateString: $dateString, temperature: $temperature, time: $time, humidity: $humidity, date: $dates, icon: $icon")

            val forecastEntry = ForecastEntry(time, temperature, humidity, dates, icon, mainn)

            // Check if the day already exists in the list
            val existingDay = forecastData.find { day -> day.date == dateString }
            if (existingDay != null) {
                existingDay.entries.add(forecastEntry)

            } else {
                forecastData.add(ForecastDay(dateString, mutableListOf(forecastEntry)))
            }
        }

        return forecastData
    }
}