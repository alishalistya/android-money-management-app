package com.example.tubespbd

import android.content.Context
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location

class TransactionManager(private val context: Context, private val locationManager: LocationManager) {
    fun getLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    fun getLocationString(): String {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Location Permissions not granted"
        }
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        return if (location != null) {
            "${location.latitude}, ${location.longitude}"
        } else {
            "Location not available"
        }
    }

}