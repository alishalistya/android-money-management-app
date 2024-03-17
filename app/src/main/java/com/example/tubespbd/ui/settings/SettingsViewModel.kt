package com.example.tubespbd.ui.settings

import android.app.Activity
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.utils.SaveExcelService
import kotlinx.coroutines.launch
import java.time.temporal.TemporalAmount

class SettingsViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
    val text: LiveData<String> = _text

    // Transaction Data
    val allTransactions: LiveData<List<Transaction>> = transactionRepository.getAllTransactionsLiveData();
    fun getAllTransactionsLiveData() = viewModelScope.launch {
        transactionRepository.getAllTransactionsLiveData()
    }

    // Save to Excel
    fun saveToExcel(activity: Activity) {
        val transactions = allTransactions.value ?: emptyList()
        val saveExcelService = SaveExcelService(activity)
        saveExcelService.createExcelDocument(transactions)
    }
}

class SettingsViewModelFactory(private val transactionRepository : TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}