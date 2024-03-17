package com.example.tubespbd.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionById(transactionId: Int): Transaction

    @Insert
    fun insertTransaction(transaction: Transaction): Long

    @Delete
    fun deleteTransaction(transaction: Transaction): Int

    @Update
    fun updateTransaction(transaction: Transaction): Int

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    fun deleteTransactionById(transactionId: Int): Int

    @Query("SELECT * FROM transactions")
    fun getAllTransactionsLiveData(): LiveData<List<Transaction>>

}