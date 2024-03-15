package com.example.tubespbd.database

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
}