package com.sheeban.meeqat_salaat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.sheeban.meeqat_salaat.databinding.ActivitySplashScreenBinding
import java.security.Permission

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val permissionRequestCode = 123
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val lottie = findViewById<LottieAnimationView>(R.id.lottieSS)

        lottie.speed = 3f
        lottie.playAnimation()

        checkPermission()

    }

    private fun checkPermission() {
        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), permissionRequestCode)
        }
        else{
            startMainActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode ==  permissionRequestCode){
            if (grantResults.isNotEmpty() &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startMainActivity()
            } else{
                finish()
            }
        }
    }

    fun startMainActivity(){

        Handler().postDelayed({
            val locationData = "City, Country"
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("locationData", locationData)
            startActivity(intent)
            finish()
        },3000)

    }

}