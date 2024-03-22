package com.example.tubespbd

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tubespbd.auth.TokenExpirationService
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.broadcast.TransactionReceiver
import com.example.tubespbd.database.*
import com.example.tubespbd.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var transactionManager: TransactionManager

    private val transactionReceiver = TransactionReceiver()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(transactionReceiver, IntentFilter("RANDOMIZE"), RECEIVER_EXPORTED)

        // Routing
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_graph, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        checkAndRequestLocationPermissions()

        // Start expiration timer
        val serviceIntent = Intent(applicationContext, TokenExpirationService::class.java)
        applicationContext.startService(serviceIntent)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(transactionReceiver)
    }

    private fun initializeAfterPermissionsGranted() {
        transactionManager = TransactionManager(this, locationManager)
    }

//    private fun getLocationString(): String {
//        return transactionManager.getLocationString()
//    }


//    private fun isLocationEnabled(): Boolean {
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }

    private fun hasLocationPermissions() = ActivityCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun checkAndRequestLocationPermissions() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions()
        } else {
            initializeAfterPermissionsGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            initializeAfterPermissionsGranted()
        } else {
            println("Location permission was denied by the user.")
        }
    }

    private fun navigateToLogin() {
        if (TokenManager.getToken() != null) {
            // Token exists and is valid, cannot go to login page
            return
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
    }
}
