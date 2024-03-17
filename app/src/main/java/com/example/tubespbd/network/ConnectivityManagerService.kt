package com.example.tubespbd.network

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.ui.NoConnectionActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConnectivityManagerService: Service(){

    private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://pbd-backend-2024.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getConnection(context: Context): Retrofit? {
        val networkService = NetworkService(context)
        if (!networkService.isNetworkConnected()) {
            Log.e("Network error", "No connection!")
            return null
        }
        return retrofit
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


}
