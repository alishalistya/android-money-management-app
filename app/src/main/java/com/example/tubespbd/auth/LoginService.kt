package com.example.tubespbd.auth

import android.content.Intent
import android.util.Log
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.interfaces.AuthService
import com.example.tubespbd.responses.LoginRequest
import com.example.tubespbd.responses.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginService {


    suspend fun login(email: String, password: String): String {

        // Build the retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create AuthService, using LoginRequest and returns a callback LoginResponse
        val authService = retrofit.create(AuthService::class.java)

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


}