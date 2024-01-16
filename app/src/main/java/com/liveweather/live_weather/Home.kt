package com.liveweather.live_weather

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Home : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var tvResult: TextView
    private lateinit var t1: TextView
    private lateinit var t2: TextView
    private lateinit var t3: TextView
    private lateinit var t4: TextView

    private lateinit var i1: ImageView
    private lateinit var i2: ImageView
    private lateinit var i3: ImageView
    private lateinit var i4: ImageView
    private lateinit var imageView: ImageView
    private lateinit var llongitude: String
    private lateinit var llatitude: String
    private lateinit var des: TextView
    private lateinit var mainn: TextView
    private lateinit var blurr: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        searchView = findViewById(R.id.searchView)
        tvResult = findViewById(R.id.tvResult)
        t1 = findViewById(R.id.v1)
        t2 = findViewById(R.id.v2)
        t3 = findViewById(R.id.v3)
        t4 = findViewById(R.id.v4)
        i1 = findViewById(R.id.i1)
        i2 = findViewById(R.id.i2)
        i3 = findViewById(R.id.i3)
        i4 = findViewById(R.id.i4)
        imageView = findViewById<ImageView>(R.id.idTVweather)
        mainn = findViewById(R.id.mainn)
        des = findViewById(R.id.des)
        blurr = findViewById(R.id.blur)

        val forbtn: Button = findViewById(R.id.forbtn)
        val locbtn: Button = findViewById(R.id.locbtn)

        forbtn.setOnClickListener {
            val intent = Intent(this@Home,Forecast::class.java)
            intent.putExtra("key1",llongitude)
            intent.putExtra("key2",llatitude)

            startActivity(intent)
        }

        locbtn.setOnClickListener {
            val intent = Intent(this@Home, Location::class.java)

            startActivity(intent)
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // When the user submits the query, fetch latitude and longitude
                FetchWeatherTask().execute(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // You can perform additional actions as the text changes if needed
                return false
            }
        })
    }

    private inner class FetchWeatherTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String? {
            if (params.isEmpty()) {
                return null
            }

            val cityName = params[0]
            val urlString =
                "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=5a7952157171f522c83a553f14325466&units=metric"

            return try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                try {
                    val `in`: InputStream = urlConnection.inputStream
                    val scanner = Scanner(`in`)
                    scanner.useDelimiter("\\A")

                    val hasInput = scanner.hasNext()
                    if (hasInput) {
                        scanner.next()
                    } else {
                        null
                    }
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    // Parse the JSON response
                    val jsonObject = JSONObject(result)
                    val coord = jsonObject.getJSONObject("coord")
                    val latitude = coord.getDouble("lat")
                    val longitude = coord.getDouble("lon")
                    lateinit var description : String
                    lateinit var icon : String
                    lateinit var mainner: String



                    val weatherArray = jsonObject.getJSONArray("weather")
                    if (weatherArray.length() > 0) {
                        val weatherObject = weatherArray.getJSONObject(0)
                        description = weatherObject.getString("description")
                        icon = weatherObject.getString("icon")
                        mainner = weatherObject.getString("main")
                    } else {
                        description = "No Data Available"
                    }

                    var iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                    Picasso.get().load(iconUrl).into(imageView)

                    val main = jsonObject.getJSONObject("main")
                    val humidity = main.getInt("humidity")
                    val temp = main.getDouble("temp")

                    val firstTwoCharacters = icon.take(2)

                    val bgt = findViewById<ImageView>(R.id.bg)

                    if(icon.last() == 'd'){
                        when(firstTwoCharacters) {
                            "01" -> bgt.setImageResource(R.drawable.dclear)
                            in "02","03","04" -> bgt.setImageResource(R.drawable.dcloudy)
                            in "09","10"-> bgt.setImageResource(R.drawable.drain)
                            "11" -> bgt.setImageResource(R.drawable.dstorm)
                            "13" -> bgt.setImageResource(R.drawable.dsnowy)
                            "50" -> bgt.setImageResource(R.drawable.dmisty)
                            else -> bgt.setImageResource(R.drawable.img)
                        }


                    } else if(icon.last() == 'n') {

                        when (firstTwoCharacters) {
                            "01" -> bgt.setImageResource(R.drawable.nclear)
                            in "02", "03", "04" -> bgt.setImageResource(R.drawable.ncloudy)
                            in "09", "10" -> bgt.setImageResource(R.drawable.nrain)
                            "11" -> bgt.setImageResource(R.drawable.nstorm)
                            "13" -> bgt.setImageResource(R.drawable.nsnow)
                            "50" -> bgt.setImageResource(R.drawable.nmisty)
                            else -> bgt.setImageResource(R.drawable.img)
                        }
                    }



                    llongitude = longitude.toString()
                    llatitude = latitude.toString()
                    t1.text = "$temp °C"
                    t2.text = "$humidity"
                    t3.text = "$longitude"
                    t4.text = "$latitude"
                    i1.visibility = ImageView.VISIBLE
                    i2.visibility = ImageView.VISIBLE
                    i3.visibility = ImageView.VISIBLE
                    i4.visibility = ImageView.VISIBLE

                    des.text = "$description"
                    mainn.text = "$mainner"
                    blurr.visibility = TextView.VISIBLE
                    tvResult.visibility = TextView.INVISIBLE

                } catch (e: JSONException) {
                    tvResult.visibility = TextView.VISIBLE
                    e.printStackTrace()
                    tvResult.text = "Error parsing response"
                }
            } else {
                tvResult.visibility = TextView.VISIBLE
                tvResult.text = "Error fetching data"
            }
        }
    }
}
