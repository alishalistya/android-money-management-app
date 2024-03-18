package com.example.tubespbd.billuploads

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.tubespbd.auth.CredentialsManager
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.interfaces.AuthService
import com.example.tubespbd.interfaces.BillServiceInterface
import com.example.tubespbd.network.ConnectivityManagerService
import com.example.tubespbd.responses.Bill
import com.example.tubespbd.responses.ItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.create
import java.io.File

class BillService: Service() {

    lateinit var billService: BillServiceInterface
    suspend fun postBill(context: Context, file: File): ItemResponse? {
        val connectivityManagerService = ConnectivityManagerService()

        val retrofit = connectivityManagerService.getConnection(context)

        if (retrofit != null) {
            billService = retrofit.create(BillServiceInterface::class.java)
        } else {
            return null
        }

        val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val token = "Bearer " + TokenManager.getToken()

        return try {
            val response = token.let { billService.post(it, filePart) }
            if (response.isSuccessful) {
                Log.d("File upload", "File upload success")
                val itemResponse: ItemResponse? = response.body()
                itemResponse
            } else {
                // Handle unsuccessful response
                Log.d("File upload", "File upload unsuccessful")
                null
            }
        } catch (e: Exception) {
            // Handle exception
            Log.e("File upload", "Network error $e")
            null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}