package com.example.tubespbd

import android.app.Application
import androidx.room.Room
import com.example.tubespbd.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.tubespbd.database.*

class App : Application() {
    lateinit var appDatabase: AppDatabase
    val transactionRepository by lazy { TransactionRepository(appDatabase.transactionDao()) }

    override fun onCreate() {
        super.onCreate()

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transaction.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Test database operations
        performDatabaseOperations()
    }

    private fun performDatabaseOperations() = CoroutineScope(Dispatchers.IO).launch {
        val locationString = "testing"

        // Insert a transaction
        val transaction = Transaction(title = "Mi Ayam", category = "Pembelian", amount = 15000f, location = locationString, tanggal = "2023-02-01 12:00:00")
        val transactionId = transactionRepository.insertTransaction(transaction)
        println("Inserted transaction with ID: $transactionId")

        // Retrieve all transactions
        val transactions = transactionRepository.getAllTransactions()
        transactions.forEach { transaction ->
            println("Transaction ID: ${transaction.id}, Title: ${transaction.title}, Category: ${transaction.category}, Amount: ${transaction.amount}, Location: ${transaction.location}, Date: ${transaction.tanggal}")
        }

        // Update last transaction
        val lastTransaction = transactions.last()
        val updatedTransaction = lastTransaction.copy(title = "Nasi Goreng")
        transactionRepository.updateTransaction(updatedTransaction)
        println("Updated transaction with ID: ${updatedTransaction.id}")

        // Retrieve all transactions after update
        val afterUpdateTransactions = transactionRepository.getAllTransactions()
        afterUpdateTransactions.forEach { transaction ->
            println("Transaction ID: ${transaction.id}, Title: ${transaction.title}, Category: ${transaction.category}, Amount: ${transaction.amount}, Location: ${transaction.location}, Date: ${transaction.tanggal}")
        }

        // Delete the last transaction
        transactionRepository.deleteTransaction(lastTransaction)
        println("Deleted transaction with ID: ${lastTransaction.id}")
    }
}