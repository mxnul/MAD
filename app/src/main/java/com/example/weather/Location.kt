package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Location : AppCompatActivity() {
    private lateinit var enterText: EditText
    private lateinit var ltemp: TextView
    private lateinit var lstatus: TextView
    private lateinit var ldate: TextView
    private lateinit var ltime: TextView
    private lateinit var lcity: TextView
    private lateinit var lvalueWind: TextView
    private lateinit var lvalueHumidity: TextView
    private lateinit var lvaluemax:TextView

    private val apiKey = "f45574ec1948a319c775ac26ded0cc8d"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        enterText = findViewById(R.id.enterText)
        ltemp = findViewById(R.id.ltemp)
        lstatus = findViewById(R.id.lstatus)
        ldate = findViewById(R.id.ldate)
        ltime = findViewById(R.id.ltime)
        lcity = findViewById(R.id.lcity)
        lvalueWind = findViewById(R.id.lvalueWind)
        lvalueHumidity = findViewById(R.id.lvalueHumidity)
        lvaluemax = findViewById(R.id.lvaluemax)

        enterText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                loadDetails(enterText.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

   /////////////isuru///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private fun loadDetails(cityName: String) {
       val url =
           "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apiKey"

       val request = JsonObjectRequest(
           Request.Method.GET, url, null,
           Response.Listener { response ->

               parseWeatherDetails(response)
           },
           Response.ErrorListener { error ->

               error.printStackTrace()
           })


       Volley.newRequestQueue(this).add(request)
   }
    private fun parseWeatherDetails(response: JSONObject) {
        try {
            val cityName = response.getString("name")
            lcity.text = cityName

            val main = response.getJSONObject("main")
            val temp = main.getString("temp")
            ltemp.text = "$temp °C"

            val windl = response.getJSONObject("wind").getString("speed")
            lvalueWind.text = "$windl"+"km/h"

            val humidityl = response.getJSONObject("main").getString("humidity")
            lvalueHumidity.text = "$humidityl"


            val maxl = response.getJSONObject("main").getString("temp_max")
            lvaluemax.text = "$maxl"


            val date = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            val time = SimpleDateFormat("h:mm a", Locale.getDefault())
            val currentDate = date.format(Date())
            val currentTime = time.format(Date())

            ldate.text = currentDate
            ltime.text = currentTime
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
