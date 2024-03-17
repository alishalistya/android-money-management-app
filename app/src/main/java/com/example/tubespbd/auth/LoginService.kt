package com.example.tubespbd.auth

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.tubespbd.interfaces.AuthService
import com.example.tubespbd.network.ConnectivityManagerService
import com.example.tubespbd.responses.LoginRequest
import com.example.tubespbd.ui.NoConnectionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginService(private val context: Context): Service() {

    private lateinit var authService: AuthService

    suspend fun login(email: String, password: String, context: Context, isFirst: Boolean): String? {

        // Build the retrofit
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://pbd-backend-2024.vercel.app/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
        val connectivityManagerService = ConnectivityManagerService()

        // Create AuthService, using LoginRequest and returns a callback LoginResponse
        val retrofit = connectivityManagerService.getConnection(context)

        if (retrofit != null) {
            authService = retrofit.create(AuthService::class.java)
        } else {
            if (isFirst) {
                navigateToNoConnection()
            }
            return null
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = authService.login(LoginRequest(email, password)).execute()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.token ?: ""
                } else {
                    Log.d("Error", "Error: $response")
                    ""
                }
            } catch (e: Exception) {
                Log.d("Network failure", "Failure: ${e.message}")
                ""
            }
        }
    }

    fun logout() {
        // Update the expiration, assumed the server time and client time is the same
        TokenManager.getToken()
    }

    private fun navigateToNoConnection() {
        val intent = Intent(context, NoConnectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}