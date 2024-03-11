package com.example.tubespbd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tubespbd.database.MyDBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MyDBHelper(this)

        // TEST DB Sementara
        // Insert
        val transactionId = dbHelper.insertTransaction("Mi Ayam", "Pembelian", 15000, "Bandung")
        println("Inserted transaction with ID: $transactionId")

        // Retrieve
        val transactions = dbHelper.getAllTransactions()
        println("All transactions:")
        for (transaction in transactions) {
            println("Transaction ID: ${transaction.id}, Title: ${transaction.title}, Amount: ${transaction.amount}")
        }

        // Update
        val updatedRows = dbHelper.updateTransaction(transactionId.toInt(), "Mi Bakso", "Pengeluaran", 20000, "Jakarta")
        println("Updated $updatedRows row(s)")

        // Retrieve yang sudah di update
        val updatedTransaction = dbHelper.getAllTransactions().find { it.id == transactionId.toInt() }
        println("Updated transaction: ${updatedTransaction?.id}, ${updatedTransaction?.title}, ${updatedTransaction?.amount}")

        // Delete
        val deletedRows = dbHelper.deleteTransaction(transactionId.toInt())
        println("Deleted $deletedRows row(s)")

        // Retrieve setelah delete
        val remainingTransactions = dbHelper.getAllTransactions()
        println("Transactions after deletion:")
        for (transaction in remainingTransactions) {
            println("Transaction ID: ${transaction.id}, Title: ${transaction.title}, Amount: ${transaction.amount}")
        }
    }
}
