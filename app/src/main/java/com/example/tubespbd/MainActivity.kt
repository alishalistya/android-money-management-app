package com.example.tubespbd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tubespbd.database.MyDBHelper
import com.example.tubespbd.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDBHelper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Routing
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Database
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
