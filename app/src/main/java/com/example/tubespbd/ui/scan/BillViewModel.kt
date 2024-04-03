package com.example.tubespbd.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.ui.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    // Transaction Data
    val allTransactions: LiveData<List<Transaction>> = transactionRepository.getAllTransactionsLiveData();
    fun insertTransactions(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.insertTransaction(transaction)
    }
}

class BillViewModelFactory(private val transactionRepository : TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}