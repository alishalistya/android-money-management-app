package com.example.tubespbd.auth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.annotation.RequiresApi
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.MainActivity
import com.example.tubespbd.auth.TokenManager.saveToken
import com.example.tubespbd.interfaces.TokenExpirationServiceInterface
import com.example.tubespbd.responses.CheckResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenExpirationService: Service() {
    // Background service
    private val scope = CoroutineScope(Dispatchers.Default)
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pbd-backend-2024.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var loginService: LoginService = LoginService(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startTokenExpirationCheck()
        return START_STICKY
    }


    private fun startForegroundService() {
        // Create notification for foreground service
        val channelId = createNotificationChannel("token_service", "Token Expiration Service")

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Token Expiration Service")
            .setContentText("Checking token expiration...")
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(id: String, name: String): String {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return id
    }

    private fun startTokenExpirationCheck() {
        scope.launch {
            while (true) {
                delay(TimeUnit.MINUTES.toMillis(5)) // Check every 5 minutes

                // Check token expiration
                isTokenExpired()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun isTokenExpired(): Boolean {
        val token = TokenManager.getToken()
        var isExpired = false
        val serviceInterface = retrofit.create(TokenExpirationServiceInterface::class.java)
        serviceInterface.check("Bearer $token").enqueue(object : Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val checkResponse = response.body()
                    if (checkResponse != null) {
                        isExpired = false
                        Log.d("Active", "Token is still active")
                    }
                } else if (response.code() == 401) {
                    isExpired = true
                    Log.d("Expired", "Token has expired!")

                    // If user checks keep logged in, then re-login the user with the saved credentials
                    val preferencesManager = PreferencesManager(applicationContext)
                    if (preferencesManager.sharedPreferences.getBoolean("keepLoggedIn", false)) {
                        GlobalScope.launch {
                            val tokenLogin = CredentialsManager.getEmail()?.let {
                                CredentialsManager.getPassword()?.let { it1 ->
                                    loginService.login(
                                        it, it1, applicationContext, false)
                                }
                            }

                            if (tokenLogin != null) {
                                saveToken(tokenLogin)
                                Log.d("Token", "New token has been saved!")
                            }
                        }

                    } else {
                        // Not keep logged in, initiate logout
                        Log.d("Redirect", "Token expired, logging out")
                        loginService.logout()
                        // Navigate to login page again
                        navigateToLogin()
                    }

                } else {
                    // Handle unsuccessful response
                    Log.e("TokenExpirationService", "Token check failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                // Handle network failure
                Log.e("TokenExpirationService", "Token check failed: ${t.message}", t)
            }
        })

        return isExpired
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Cancel coroutine scope when service is destroyed
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}