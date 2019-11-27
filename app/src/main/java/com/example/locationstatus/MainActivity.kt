package com.example.locationstatus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    private lateinit var lm: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationReq: LocationRequest
    private lateinit var titleLabel: TextView

    private var REQUEST_CODE_LOCATION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        this.titleLabel = findViewById(R.id.titleLabel)

        //  https://developer.android.com/training/location/change-location-settings
        this.locationReq = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        this.lm = LocationServices.getFusedLocationProviderClient(this)
        this.locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.locations.first()
                location ?: return
                titleLabel.text = "${location.latitude} ${location.longitude}"
            }
        }
    }

    override fun onStop() {
        super.onStop()
        this.stopUpdateLocation()
    }

    override fun onStart() {
        super.onStart()
        this.requestPermission()
    }

    private fun requestPermission() {
        val permissionRes = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionRes != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
        } else {
            this.startUpdateLocation()
        }
    }

    private fun startUpdateLocation() {
        this.lm.requestLocationUpdates(this.locationReq, this.locationCallback, null)
    }

    private fun stopUpdateLocation() {
        this.lm.removeLocationUpdates(this.locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    this.startUpdateLocation()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
