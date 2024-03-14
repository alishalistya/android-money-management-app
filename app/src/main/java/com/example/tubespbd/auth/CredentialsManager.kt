package com.example.tubespbd.auth

import android.content.SharedPreferences
import java.util.Date

object CredentialsManager {
    // initialization for variable
    private lateinit var sharedPreferences: SharedPreferences
    private const val EMAIL = "email"
    private const val PASSWORD = "password"

    fun init(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun saveCredentials(email: String, password: String) {
        with(CredentialsManager.sharedPreferences.edit()) {
            putString(CredentialsManager.EMAIL, email)
            putString(CredentialsManager.PASSWORD, password)
            apply()
        }
    }

    fun getEmail(): String? {
        return CredentialsManager.sharedPreferences.getString(CredentialsManager.EMAIL, null)
    }

    fun getPassword(): String? {
        return CredentialsManager.sharedPreferences.getString(CredentialsManager.PASSWORD, null)
    }

    private fun clearData() {
        with(CredentialsManager.sharedPreferences.edit()) {
            remove(CredentialsManager.EMAIL)
            remove(CredentialsManager.PASSWORD)
            apply()
        }
    }
}