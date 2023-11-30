package com.example.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.location.Address
import android.location.Geocoder
import android.widget.Button
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var progressDialog: ProgressDialog? = null
    private lateinit var temp: TextView
    private lateinit var status: TextView
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var valueWind: TextView
    private lateinit var valueHumidity: TextView
    private lateinit var valuemax:TextView
    private lateinit var btnLoc:Button


    private val apiKey = "f45574ec1948a319c775ac26ded0cc8d"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temp = findViewById(R.id.temp)
        status = findViewById(R.id.status)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        valueWind = findViewById(R.id.valueWind)
        valueHumidity = findViewById(R.id.valueHumidity)
        valuemax = findViewById(R.id.valuemax)
        btnLoc= findViewById(R.id.btnLoc)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()


        getCurrentLocationAndWeather()

        btnLoc.setOnClickListener {
            // Navigate to Locations.Kt page
            val intent = Intent(this,Location::class.java)
            startActivity(intent)
        }
    }

   private fun getCurrentLocationAndWeather() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        loadWeather(location.latitude, location.longitude)
                    } else {
                        dismissProgressDialog()
                        showErrorToast("Location not available")
                    }
                }
                .addOnFailureListener { e ->
                    dismissProgressDialog()
                    Log.e("Location", " location Error", e)
                    showErrorToast(" location Error")
                }
        } catch (e: SecurityException) {
            dismissProgressDialog()
            Log.e("Location", "Security exception: ${e.message}")
            showErrorToast("Location permission denied")
        }
    }

    private fun loadWeather(latitude: Double, longitude: Double) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$apiKey"
        showProgressDialog("Loading")

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { data ->
                try {
                    val description =
                        data.getJSONArray("weather").getJSONObject(0).getString("description")
                    status.text = description

                    val temperature = data.getJSONObject("main").getString("temp")

                    temp.text = "$temperature"+"°C"

                    val wind = data.getJSONObject("wind").getString("speed")
                    valueWind.text = "$wind"+"km/h"

                    val humidity = data.getJSONObject("main").getString("humidity")
                    valueHumidity.text = "$humidity"


                    val max = data.getJSONObject("main").getString("temp_max")
                    valuemax.text = "$max"

                    val dateV = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                    val timeV = SimpleDateFormat("h:mm a", Locale.getDefault())

                    val currentDate = dateV.format(Date())
                    val currentTime = timeV.format(Date())

                    date.text = currentDate
                    time.text = currentTime


                    dismissProgressDialog()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                    dismissProgressDialog()
                }
            },
            { error ->
                Log.e("Response", error.toString())
                // Handle error here
                dismissProgressDialog()
                showErrorToast("Error fetching weather data")
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
