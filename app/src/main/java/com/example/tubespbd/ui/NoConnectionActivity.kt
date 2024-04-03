package com.example.tubespbd.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.R

class NoConnectionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_404)
        Log.d("Page", "No connection page")

        val reloadButton = findViewById<Button>(R.id.reload_button)
        reloadButton.setOnClickListener {
            navigateToLogin()
        }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }
}
