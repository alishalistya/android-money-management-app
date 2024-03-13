package com.example.tubespbd.interfaces
import com.example.tubespbd.responses.LoginRequest
import com.example.tubespbd.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}