package com.sheeban.meeqat_salaat

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sheeban.meeqat_salaat.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var cityName : EditText
    lateinit var countryName : EditText
    lateinit var btnLocation : Button
    lateinit var fajr : TextView
    lateinit var sunrise : TextView
    lateinit var dhuhr : TextView
    lateinit var asr : TextView
    lateinit var sunset : TextView
    lateinit var maghrib : TextView
    lateinit var isha : TextView
    lateinit var midnight : TextView
    lateinit var firstthird : TextView
    lateinit var lastthird : TextView
    lateinit var hijri : TextView
    lateinit var month : TextView
    lateinit var monthAr : TextView

    lateinit var consTimings : ConstraintLayout

    private val API_BASE_URL = "https://api.aladhan.com/v1/timingsByCity/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        spinner()
        defaultDate()

        cityName = binding.etCity
        countryName = binding.etCountry
        fajr = binding.tvFajr
        sunrise = binding.tvSunrise
        dhuhr = binding.tvDhuhr
        asr = binding.tvAsr
        sunset = binding.tvSunset
        maghrib = binding.tvMaghrib
        isha = binding.tvIsha
        midnight = binding.tvMidnight
        firstthird = binding.tvFirstthird
        lastthird = binding.tvLastthird
        hijri = binding.tvHijri
        month = binding.tvMonth
        monthAr = binding.tvMonthArabic

        consTimings = binding.constraintTimings

        defaultLocationShow()

        btnLocation = binding.location
        btnLocation.setOnClickListener {
            location()
        }

        binding.btnCalender.setOnClickListener {
            setDate()
        }

        consTimings.visibility = View.GONE



        binding.btnGetTimings.setOnClickListener {
            binding.lottieLoading.visibility = View.VISIBLE
            val selectedDate = binding.btnCalender.text.toString()
            val city = cityName.text.toString()
            val country = countryName.text.toString()
            val methodIndex = binding.spinner.selectedItemPosition

            val apiUrl = "$API_BASE_URL$selectedDate?city=$city&country=$country&method=$methodIndex"

            fetchPrayerTimings(apiUrl)
        }





    }

    private fun defaultLocationShow() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
            location()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun fetchPrayerTimings(apiUrl: String) {
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET, apiUrl, null,
            { response ->
                // Handle the JSON response here and update the TextViews
                val timingsObject = response.getJSONObject("data").getJSONObject("timings")
                fajr.text = timingsObject.getString("Fajr")
                sunrise.text = timingsObject.getString("Sunrise")
                dhuhr.text = timingsObject.getString("Dhuhr")
                asr.text = timingsObject.getString("Asr")
                sunset.text = timingsObject.getString("Sunset")
                maghrib.text = timingsObject.getString("Maghrib")
                isha.text = timingsObject.getString("Isha")
                midnight.text = timingsObject.getString("Midnight")
                firstthird.text = timingsObject.getString("Firstthird")
                lastthird.text = timingsObject.getString("Lastthird")
                hijri.text = response.getJSONObject("data").getJSONObject("date").getJSONObject("hijri").getString("date")
                month.text = response.getJSONObject("data").getJSONObject("date").getJSONObject("hijri").getJSONObject("month").getString("en")
                monthAr.text = response.getJSONObject("data").getJSONObject("date").getJSONObject("hijri").getJSONObject("month").getString("ar")
                binding.lottieLoading.visibility = View.GONE
                consTimings.visibility = View.VISIBLE

                Toast.makeText(this, "Here are your timings for ${cityName.text}", Toast.LENGTH_LONG).show()
            },
            { error ->
                // Handle any errors that occurred during the request
                Log.e("VolleyError", error.toString())
            }
        )

        queue.add(request)
    }

    private fun spinner(){

        val spinner = binding.spinner
        val items = arrayOf(
            "Shia",
            "Sunni"
        )

        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.setSelection(1)

    }

    private fun defaultDate(){
        val btnCalender = binding.btnCalender

        // Set the default text to the current date
        val currentDate = Calendar.getInstance()
        val dateFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        btnCalender.text = simpleDateFormat.format(currentDate.time)

    }

    private fun setDate() {

        val currentDate = Calendar.getInstance()
        val dateFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())


        val datePicker = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Update the button's text with the selected date
                binding.btnCalender.text = simpleDateFormat.format(selectedDate.time)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun location() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Handle the location data here
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )
                        if (addresses != null && addresses.isNotEmpty()) {
                            val city = addresses[0].locality
                            val country = addresses[0].countryName

                            // Log the retrieved location data for debugging
                            Log.d("LocationData", "City: $city, Country: $country")

                            // Update your EditText widgets with the location data
                            cityName.setText(city)
                            countryName.setText(country)
                        } else {
                            // Handle the case where geocoder data is not available
                            Log.e("LocationData", "Geocoder data is not available")
                        }
                    } else {
                        // Handle the case where location is not available
                        Log.e("LocationData", "Location is not available")
                        Toast.makeText(this, "Location is not available", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Handle the case where location permissions are not granted
            Log.e("LocationData", "Location permissions are not granted")
            Toast.makeText(this, "Enable Location Permission", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}

