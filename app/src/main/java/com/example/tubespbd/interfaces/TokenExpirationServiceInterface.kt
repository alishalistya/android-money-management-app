package com.example.tubespbd.interfaces

import com.example.tubespbd.responses.CheckResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenExpirationServiceInterface {

    @POST("api/auth/token")
    fun check(@Header("Authorization") token: String): Call<CheckResponse>
}