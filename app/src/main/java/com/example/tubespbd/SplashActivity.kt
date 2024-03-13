package com.example.tubespbd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity: AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // Using Kotlin Coroutines to delay transition to LoginActivity
        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DELAY)
            // Start LoginActivity after the delay
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            // Finish SplashActivity so that it's not accessible by pressing back button
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the coroutine if the activity is destroyed
        CoroutineScope(Dispatchers.Main).cancel()
    }

}