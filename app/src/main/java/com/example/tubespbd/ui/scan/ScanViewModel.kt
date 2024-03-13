package com.example.tubespbd.ui.scan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "This is scan fragment"
    }
    val text: LiveData<String> = _text
}