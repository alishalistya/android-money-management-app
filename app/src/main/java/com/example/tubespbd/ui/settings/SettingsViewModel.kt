package com.example.tubespbd.ui.settings

import android.app.Activity
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.utils.MailIntentUtil
import com.example.tubespbd.utils.SaveExcelUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    fun saveToExcel(activity: Activity, fileType: String = "XLSX") {
        val transactions = allTransactions.value ?: emptyList()
        val fileName = LocalDate.now().toString()
        val saveExcelUtil = SaveExcelUtil(activity)
        saveExcelUtil.createExcelDocument(transactions, fileName, fileType)
    }

    fun prepareEmail(activity: Activity, fragment: Fragment) {
        val saveExcelUtil = SaveExcelUtil(activity)
        val recipient = "alisha.listya@gmail.com"
        val subject = "Subject of the Email"
        val message = "Message body of the email."
        val mailIntent = MailIntentUtil()

        // Access the value of LiveData
        allTransactions.value?.let { transactionsList ->
            CoroutineScope(Dispatchers.Main).launch {
                val file = saveExcelUtil.generateDocument(transactionsList)

                if (file != null) {
                    val fileUri = FileProvider.getUriForFile(
                        fragment.requireContext(),
                        "com.example.tubespbd.fileprovider",
                        file
                    )
//                    Log.d("Mail Service", fileUri.toString())
                    mailIntent.sendEmail(fragment.requireContext(), recipient, subject, message, fileUri)
//                    Log.d("Mail Service", "Mail Created!")
                } else {
                    Log.e("prepareEmail", "Failed to generate the document.")
                }
            }
        } ?: run {
            Log.e("prepareEmail", "Transactions data is null or not available.")
        }
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