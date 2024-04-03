package com.example.tubespbd

import android.content.Context
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.Geocoder
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class TransactionManager(private val context: Context, private val locationManager: LocationManager) {
    fun getLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    suspend fun getLocationString(): String = withContext(Dispatchers.IO) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@withContext "Location Permissions not granted"
        }
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        return@withContext if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = withTimeoutOrNull(5000) { // Timeout 5 detik
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }
            if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0)
            } else {
                "${location.latitude}, ${location.longitude}" // Fallback ke latitude longitude kalau geocoder gadapet lokasi
            }
        } else {
            "Location not available"
        }
    }
}
