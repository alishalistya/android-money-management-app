// HomeViewModel.kt
package com.example.tubespbd.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    val transactions: List<Transaction> = transactionRepository.getAllTransactions()

    init {
        viewModelScope.launch {
            transactionRepository.refreshTransactions()
        }
    }
}
