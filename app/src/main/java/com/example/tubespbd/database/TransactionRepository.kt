package com.example.tubespbd.database

import androidx.lifecycle.LiveData

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

    fun getAllTransactionsLiveData(): LiveData<List<Transaction>> {
        return transactionDao.getAllTransactionsLiveData()
    }

    suspend fun refreshTransactions() {
        // Perform data refresh operations if needed
    }
}