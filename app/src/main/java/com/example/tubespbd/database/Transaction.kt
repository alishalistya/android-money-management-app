package com.example.tubespbd.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val category: String?,
    val amount: Float?,
    val location: String?,
    val tanggal: String?
)
data class TransactionSum(
    val category: String,
    val amount: Float
)