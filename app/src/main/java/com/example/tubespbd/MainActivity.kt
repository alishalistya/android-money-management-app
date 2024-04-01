package com.example.tubespbd

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
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
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var transactionManager: TransactionManager

    private val transactionReceiver = TransactionReceiver()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sideNavigationView: NavigationView
    private lateinit var navController: NavController


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(transactionReceiver, IntentFilter("RANDOMIZE"), RECEIVER_EXPORTED)

        // Routing
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

//        val navView: BottomNavigationView = binding.navView
        bottomNavigationView = findViewById(R.id.nav_view)
        sideNavigationView = findViewById(R.id.side_nav_view)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
//        bottomNavigationView.setupWithNavController(navController)

        sideNavigationView.visibility = View.GONE

//        val navController = findNavController(R.id.nav_host_fragment_activity_main)

//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_graph, R.id.navigation_settings
//            )
//        )

//        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
        sideNavigationView.setupWithNavController(navController)
        checkAndRequestLocationPermissions()

        // Start expiration timer
        val serviceIntent = Intent(applicationContext, TokenExpirationService::class.java)
        applicationContext.startService(serviceIntent)

        adjustNavigationForOrientation(resources.configuration.orientation)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(transactionReceiver)
    }

    private fun adjustNavigationForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomNavigationView.visibility = View.GONE
            sideNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
            sideNavigationView.visibility = View.GONE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustNavigationForOrientation(newConfig.orientation)
    }


    private fun initializeAfterPermissionsGranted() {
        transactionManager = TransactionManager(this, locationManager)
    }

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
