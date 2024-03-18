package com.example.tubespbd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tubespbd.R.id.button_masuk
import com.example.tubespbd.auth.CredentialsManager
import com.example.tubespbd.auth.EmailValidator
import com.example.tubespbd.auth.LoginService
import com.example.tubespbd.auth.PreferencesManager
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.network.ConnectivityManagerService
import com.example.tubespbd.ui.NoConnectionActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
// TODO: UI

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var errorMessageTextView: TextView
    private lateinit var emailErrorTextView: TextView
    private lateinit var keepLoggedInCheckbox: CheckBox
    private lateinit var loginService: LoginService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip login screen if already logged in
        if (::preferencesManager.isInitialized && TokenManager.getToken() != null) {
            // Token exists and is valid, navigate to the next screen
            navigateToNextActivity()
            return // Skip further execution of onCreate()
        }

        // start connectivity manager service
        val managerIntent = Intent(applicationContext, ConnectivityManagerService::class.java)
        applicationContext.startService(managerIntent)

        loginService = LoginService(applicationContext)

        setContentView(R.layout.activity_login)

        // Initialize EncryptedSharedPreferences for saving the token
        initEncryptedSharedPreferences()

        // find error messages, view is still GONE
        errorMessageTextView = findViewById(R.id.errorMessageTextView)
        emailErrorTextView = findViewById(R.id.emailErrorTextView)
        // Keeping the user logged in
        keepLoggedInCheckbox = findViewById<CheckBox>(R.id.keepLoggedInCheckBox)
        val keepLoggedIn = preferencesManager.sharedPreferences.getBoolean("keepLoggedIn", false)
        keepLoggedInCheckbox.isChecked = keepLoggedIn

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

    @OptIn(DelicateCoroutinesApi::class)
    private fun attemptLogin(email: String, password: String) {
        // Launch a coroutine to perform login asynchronously
        GlobalScope.launch {
            val token = loginService.login(email, password, applicationContext)

            if (token != null) {
                if (token.isNotEmpty()) {
                    TokenManager.saveToken(token)
                    runOnUiThread {
                        Log.d("Token", "Token saved!")
                        // Error message validation is gone
                        errorMessageTextView.visibility = View.GONE

                        // Save credentials if keepLoggedIn Checkbox is True
                        if (keepLoggedInCheckbox.isChecked) {
                            CredentialsManager.saveCredentials(email, password)

                            // Save that the user wants to be kept logged in
                            with(preferencesManager.sharedPreferences.edit()) {
                                putBoolean("keepLoggedIn", true)
                                apply()
                            }

                            Log.d("Login", "Keep logged in active")
                        }

                        // Navigate to main activity
                        navigateToNextActivity()
                    }

                } else {
                    runOnUiThread {
                        // Error message validation is visible
                        errorMessageTextView.visibility = View.VISIBLE
                    }
                }
            } else {
                navigateToNoConnection()
            }
        }
    }

    private fun initEncryptedSharedPreferences() {
        preferencesManager = PreferencesManager(applicationContext)
    }


    private fun navigateToNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToNoConnection() {
        val intent = Intent(this, NoConnectionActivity::class.java)
        startActivity(intent)
    }

}
