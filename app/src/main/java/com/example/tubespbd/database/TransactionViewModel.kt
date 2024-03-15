//package com.example.tubespbd.database
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//
//class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
//
//    fun getAllTransactions() = repository.getAllTransactions()
//
//    fun insert(transaction: Transaction) = viewModelScope.launch {
//        repository.insertTransaction(transaction)
//    }
//
//    fun delete(transaction: Transaction) = viewModelScope.launch {
//        repository.deleteTransaction(transaction)
//    }
//
//    fun update(transaction: Transaction) = viewModelScope.launch {
//        repository.updateTransaction(transaction)
//    }
//}