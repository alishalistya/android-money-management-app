package com.example.tubespbd.database

import androidx.lifecycle.LiveData
import java.util.Date
import kotlin.random.Random

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }

    fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction): Int {
        return transactionDao.deleteTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction): Int {
        return transactionDao.updateTransaction(transaction)
    }

    suspend fun getTransactionById(transactionId: Int): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }

    suspend fun deleteTransactionById(transactionId: Int) {
        transactionDao.deleteTransactionById(transactionId)
    }

    fun getAllTransactionsLiveData(): LiveData<List<Transaction>> {
        return transactionDao.getAllTransactionsLiveData()
    }

    fun getTransactionsSumByCategoryLiveData(): LiveData<List<TransactionSum>> {
        return transactionDao.getTransactionsSumByCategory()
    }

    // buat random transaction + bool func
    fun generateAndInsertRandomTransaction(): Boolean {
        val randomTransaction = generateRandomTransaction()
        val transactionId = transactionDao.insertTransaction(randomTransaction)
        return isTransactionInsertedSuccessfully(transactionId)
    }
    fun generateRandomTransaction(): Transaction {
        val randomTitle = generateRandomTitle()
        val categories = listOf("Pengeluaran", "Pemasukan")
        val randomCategory = categories.random()
        val randomAmount = Random.nextFloat() * 1000f
        val randomLocation = generateRandomLocation()
        val currentDate = Date().toString()
        return Transaction(
            category = randomCategory,
            amount = randomAmount,
            tanggal = currentDate,
            title = randomTitle,
            location = randomLocation
        )
    }
    fun generateRandomTitle(): String {
        val randomSuffix = String.format("%03d", Random.nextInt(1000))
        return "title$randomSuffix"
    }
    fun generateRandomLocation(): String {
        // Generate a random location string (for example, "Random Location 123")
        return "Random Location ${Random.nextInt(1000)}"
    }
    fun isTransactionInsertedSuccessfully(transactionId: Long): Boolean {
        return transactionId > 0
    }
}