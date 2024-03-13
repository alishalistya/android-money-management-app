package com.example.tubespbd.auth

import android.content.SharedPreferences
import java.util.Date

object TokenManager {
    // initialization for variable
    private lateinit var sharedPreferences: SharedPreferences
    private const val TOKEN_KEY = "token"
    private const val EXPIRATION_KEY = "expiration"

    fun init(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun saveToken(token: String, expirationDate: Date) {
        with(sharedPreferences.edit()) {
            putString(TOKEN_KEY, token)
            putLong(EXPIRATION_KEY, expirationDate.time)
            apply()
        }
    }

    fun getToken(): String? {
        val expiration = sharedPreferences.getLong(EXPIRATION_KEY, -1)
        // Check if token expiration is set and is expired
        if (expiration != -1L && expiration < System.currentTimeMillis()) {
            // Token has expired
            clearToken()
            return null
        }
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    private fun clearToken() {
        with(sharedPreferences.edit()) {
            remove(TOKEN_KEY)
            remove(EXPIRATION_KEY)
            apply()
        }
    }
}