package com.liveweather.live_weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class Location : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 123
    }

    private lateinit var tvResult: TextView
    private lateinit var t1: TextView
    private lateinit var t2: TextView
    private lateinit var t3: TextView
    private lateinit var t4: TextView
    private lateinit var ww1: TextView
    private lateinit var ww2: TextView
    private lateinit var ww3: TextView
    private lateinit var ww4: TextView
    private lateinit var imageView: ImageView
    private lateinit var des: TextView
    private lateinit var mainn: TextView
    private lateinit var name: TextView
    private lateinit var llongitude: String
    private lateinit var llatitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        tvResult = findViewById(R.id.tvResult)
        t1 = findViewById(R.id.v1)
        t2 = findViewById(R.id.v2)
        t3 = findViewById(R.id.v3)
        t4 = findViewById(R.id.v4)
        ww1 = findViewById(R.id.w1)
        ww2 = findViewById(R.id.w2)
        ww3 = findViewById(R.id.w3)
        ww4 = findViewById(R.id.w4)
        imageView = findViewById(R.id.idTVweather)
        mainn = findViewById(R.id.mainn)
        des = findViewById(R.id.des)
        name = findViewById(R.id.name)

        val locbtn: Button = findViewById(R.id.locbtn)

        val forbtn: Button = findViewById(R.id.forbtn)
        locbtn.setOnClickListener {
            val intent = Intent(this@Location, com.liveweather.live_weather.Home::class.java)

            startActivity(intent)
        }

        forbtn.setOnClickListener {
            val intent = Intent(this@Location,Forecast::class.java)
            intent.putExtra("key1",llongitude)
            intent.putExtra("key2",llatitude)

            startActivity(intent)
        }

        // Check and request location permissions at runtime
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, proceed with location-related operations
            retrieveLocation()
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }

    // Override onRequestPermissionsResult to handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with location-related operations
                    retrieveLocation()
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }

    private fun retrieveLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, proceed with location-related operations
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)

            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                            val urlString =
                                "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=bc7b5d35939d2152c5319a7c5247387d&units=metric"

                            // Use AsyncTask for network operations
                            FetchWeatherTask().execute(urlString)
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            } catch (securityException: SecurityException) {
                securityException.printStackTrace()
            }
        } else {
            // Permission not granted, handle accordingly
        }
    }

    // AsyncTask for network operations
    private inner class FetchWeatherTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String? {
            if (params.isEmpty()) {
                return null
            }

            val urlString = params[0]
            return fetchDataFromUrl(urlString)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                try {
                    // Parse the JSON response
                    val jsonObject = JSONObject(result)
                    val coord = jsonObject.getJSONObject("coord")
                    val latitude = coord.getDouble("lat")
                    val longitude = coord.getDouble("lon")
                    val names = jsonObject.getString("name")
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


                    if(longitude != null && latitude != null){
                        llongitude = longitude.toString()
                        llatitude = latitude.toString()
                    }
                    t1.text = "$temp °C"
                    t2.text = "$humidity"
                    t3.text = "$longitude"
                    t4.text = "$latitude"
                    ww1.text = "Temperature :"
                    ww2.text = "Humidity :"
                    ww3.text = "Longitude :"
                    ww4.text = "Latitude :"
                    des.text = "$description"
                    mainn.text = "$mainner"
                    name.text = "$names"


                } catch (e: JSONException) {
                    e.printStackTrace()
                    tvResult.text = "Error parsing response"
                }
            } else {
                tvResult.text = "Error fetching data"
            }
        }
    }

    private fun fetchDataFromUrl(urlString: String): String? {
        val url = URL(urlString)
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val `in`: InputStream = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            if (scanner.hasNext()) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}
