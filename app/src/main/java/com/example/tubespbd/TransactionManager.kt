package com.example.tubespbd

import android.content.Context
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.tubespbd.database.MyDBHelper
import com.example.tubespbd.database.MyDBHelper.Transaction

class TransactionManager(private val context: Context, private val locationManager: LocationManager) {

    private val dbHelper = MyDBHelper(context)

    fun insertTransaction(title: String, type: String, amount: Int, locationString: String): Long {
        val locationString = getLocationString()
        return dbHelper.insertTransaction(title, type, amount, locationString)
    }

    fun getAllTransactions(): List<Transaction> {
        return dbHelper.getAllTransactions()
    }

    fun updateTransaction(id: Int, title: String, type: String, amount: Int, location: String): Int {
        return dbHelper.updateTransaction(id, title, type, amount, location)
    }

    fun deleteTransaction(id: Int): Int {
        return dbHelper.deleteTransaction(id)
    }

    fun getLocationString(): String {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
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
