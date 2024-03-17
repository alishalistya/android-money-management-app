package com.example.tubespbd

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tubespbd.databinding.ActivityMainBinding
import android.location.LocationManager
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tubespbd.auth.TokenExpirationService
import com.example.tubespbd.auth.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.google.android.material.navigation.NavigationBarView

import com.example.tubespbd.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var transactionManager: TransactionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        navView.setOnItemSelectedListener { item ->
            Log.d("MainActivity", "Item selected: ${item.itemId}")
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    Log.d("MainActivity", "Navigating to HistoryActivity")
                    val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        checkAndRequestLocationPermissions()

        // Start expiration timer
        val serviceIntent = Intent(applicationContext, TokenExpirationService::class.java)
        applicationContext.startService(serviceIntent)

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
