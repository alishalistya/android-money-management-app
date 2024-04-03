package com.example.tubespbd.interfaces

import com.example.tubespbd.responses.ItemResponse
import com.example.tubespbd.responses.LoginRequest
import com.example.tubespbd.responses.LoginResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BillServiceInterface {
    @Multipart
    @POST("api/bill/upload")
    suspend fun post(@Header("Authorization") authToken: String, @Part filePart: MultipartBody.Part): Response<ItemResponse>

}