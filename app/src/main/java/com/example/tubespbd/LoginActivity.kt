package com.example.tubespbd

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.example.tubespbd.R.id.button_masuk
import com.example.tubespbd.auth.EmailValidator
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.interfaces.AuthService
import com.example.tubespbd.responses.LoginRequest
import com.example.tubespbd.responses.LoginResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Date

class LoginActivity : AppCompatActivity() {
// TODO: Client side checking and UI

    private lateinit var sharedPreferences: EncryptedSharedPreferences
    lateinit var errorMessageTextView: TextView
    lateinit var emailErrorTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (::sharedPreferences.isInitialized && TokenManager.getToken() != null) {
            // Token exists and is valid, navigate to the next screen
            navigateToNextActivity()
            return // Skip further execution of onCreate()
        }

        setContentView(R.layout.activity_login)

        // Initialize EncryptedSharedPreferences for saving the token
        initEncryptedSharedPreferences()

        // find error messages, view is still GONE
        errorMessageTextView = findViewById(R.id.errorMessageTextView)
        emailErrorTextView = findViewById(R.id.emailErrorTextView)

        // setup login button to trigger login function
        val loginButton = findViewById<Button>(button_masuk)
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            if (EmailValidator.isValidEmail(email)) {
                // Email error message validation is gone (client side checking)
                emailErrorTextView.visibility = View.GONE
                // call attemptLogin
                attemptLogin(email, password)
            } else {
                // Email error message validation is visible (client side checking)
                emailErrorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun attemptLogin(email: String, password: String) {

        // Build the retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginRequest = LoginRequest("13521089@std.stei.itb.ac.id", "password_13521089")

        // Create AuthService, using LoginRequest and returns a callback LoginResponse
        val authService = retrofit.create(AuthService::class.java)
        authService.login(LoginRequest(email, password)).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Get the token if successful
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    // Check if token is null
                    if (token != null) {
                        // Saved token to Token Manager
                        saveToken(token)
                        Log.d("Token", "Token saved!")

                        // Error message validation is gone
                        errorMessageTextView.visibility = View.GONE

                        // Navigate to main activity
                        navigateToNextActivity()
                    }
                } else {
                    Log.d("Error","Error: $response")
                    // Error message validation is visible
                    errorMessageTextView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle network failure
                Log.d("Network failure","Failure: ${t.message}")
            }
        })
    }

    private fun initEncryptedSharedPreferences() {
        // Create MasterKey with context to encrypt and decrypt token
        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create EncryptedSharedPreferences
        this.sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "encrypted_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences

        // Initialize TokenManager
        TokenManager.init(sharedPreferences)
    }

    private fun saveToken(token: String) {
        // Save token with 5 minutes expiration
        val expirationCalendar = Calendar.getInstance()
        expirationCalendar.time = Date()
        expirationCalendar.add(Calendar.MINUTE, 5)
        val expirationDate = expirationCalendar.time

        TokenManager.saveToken(token, expirationDate)
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
