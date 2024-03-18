package com.example.tubespbd.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tubespbd.App
import com.example.tubespbd.database.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Broadcast", "Broadcast received!")
        if (intent?.action == "RANDOMIZE") {
            println("Received randomize intent")
            // Get the application context
            val appContext = context?.applicationContext

            // Retrieve the AppDatabase instance
            val appDatabase = (appContext as App).appDatabase

            // Get the TransactionDao from the AppDatabase
            val transactionDao = appDatabase.transactionDao()

            // Create the TransactionRepository
            val transactionRepository = TransactionRepository(transactionDao)

            CoroutineScope(Dispatchers.IO).launch {
                val success = transactionRepository.generateAndInsertRandomTransaction()
                if (success) {
                    println("Successfully inserted random transaction")
                } else {
                    println("Random transaction insertion unsuccessful")
                }
            }

        }
    }
}