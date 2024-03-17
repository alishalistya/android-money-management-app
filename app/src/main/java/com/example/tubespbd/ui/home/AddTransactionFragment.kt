// AddTransactionFragment.kt
package com.example.tubespbd.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tubespbd.R
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionAdapter
import com.example.tubespbd.databinding.FragmentAddTransactionBinding
import com.example.tubespbd.databinding.FragmentHomeBinding
import java.util.Date
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.example.tubespbd.TransactionManager
import android.location.LocationManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.example.tubespbd.App
import com.example.tubespbd.database.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

import androidx.navigation.fragment.findNavController
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null // Updated binding class
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaction>()
    private lateinit var locationManager: LocationManager
    private lateinit var transactionRepository: TransactionRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false) // Updated layout inflation
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        transactionAdapter = TransactionAdapter(transactions) { transaction ->
        }

        val appDatabase = (requireActivity().application as App).appDatabase
        // Get the TransactionDao from the AppDatabase
        val transactionDao = appDatabase.transactionDao()
        // Create the TransactionRepository
        transactionRepository = TransactionRepository(transactionDao)

        val categories = arrayOf("Pemasukan", "Pengeluaran")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        val categoryEditText = view.findViewById<AutoCompleteTextView>(R.id.categoryEditText)
        categoryEditText.setAdapter(adapter)

        // Biar jadi drop down
        categoryEditText.isFocusable = false
        categoryEditText.isFocusableInTouchMode = false
        categoryEditText.setOnClickListener {
            categoryEditText.showDropDown()
        }

        // Nulis lokasi kalau nyalain lokasi
        CoroutineScope(Dispatchers.Main).launch {
            val locationString = getLocationString()
            binding.locationEditText.setText(locationString)
        }

        binding.addTransactionButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                addTransaction()
            }
            navigateBack()
        }

        binding.backButton.setOnClickListener {
            navigateBack()
        }

        CoroutineScope(Dispatchers.IO).launch {
            getAllTransactions()
        }
    }
    private fun navigateBack() {
        findNavController().navigateUp()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addTransaction() {
        val title = binding.titleEditText.text.toString()
        val category = binding.categoryEditText.text.toString()
        val amountStr = binding.amountEditText.text.toString()
        val amount = if (amountStr.isNotEmpty()) amountStr.toFloat() else 0f

        var locationString = ""
        CoroutineScope(Dispatchers.IO).launch {
            locationString = getLocationString()
        }
        val currentDate = Date()

        val newTransaction = Transaction(
            title = title,
            category = category,
            amount = amount,
            location = locationString,
            tanggal = currentDate.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.insertTransaction(newTransaction)
            getAllTransactions()
        }

        binding.titleEditText.text.clear()
        binding.categoryEditText.text.clear()
        binding.amountEditText.text.clear()

    }

    private suspend fun getAllTransactions() {
        transactions.clear()
        transactions.addAll(transactionRepository.getAllTransactions())
        withContext(Dispatchers.Main) {
            transactionAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun getLocationString(): String {
        return when {
            hasLocationPermissions() && isLocationEnabled() -> {
                val transactionManager = TransactionManager(requireContext(), locationManager)
                transactionManager.getLocationString()
            }
            !hasLocationPermissions() -> {
                "Location denied"
            }
            else -> {
                "Location unavailable"
            }
        }
    }


    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}