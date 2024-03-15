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
import com.example.tubespbd.databinding.FragmentHistoryBinding
import com.example.tubespbd.databinding.FragmentHomeBinding
import java.util.Date
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.tubespbd.TransactionManager
import android.location.LocationManager
class HomeFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaction>()
    private lateinit var locationManager: LocationManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        transactionAdapter = TransactionAdapter(transactions)
        binding.transactionRecyclerView.adapter = transactionAdapter

        binding.addTransactionButton.setOnClickListener {
            addTransaction()
        }
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

        // Get location and date
        val locationString = getLocationString()
        val currentDate = Date()

        val newTransaction = Transaction(
            title = title,
            category = category,
            amount = amount,
            location = locationString,
            tanggal = currentDate.toString() // Assign current datetime to tanggal
        )

        transactions.add(newTransaction)
        transactionAdapter.notifyDataSetChanged()

        // Clear input fields
        binding.titleEditText.text.clear()
        binding.categoryEditText.text.clear()
        binding.amountEditText.text.clear()
    }

    private fun getLocationString(): String {
        return when {
            hasLocationPermissions() && isLocationEnabled() -> {
                // Get location if permission is granted and location is enabled
                val transactionManager = TransactionManager(requireContext(), locationManager)
                transactionManager.getLocationString()
            }
            !hasLocationPermissions() -> {
                // Location permissions not granted
                "Location denied"
            }
            else -> {
                // Location not available
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

