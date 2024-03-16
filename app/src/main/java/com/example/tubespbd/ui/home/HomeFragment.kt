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
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.App
import com.example.tubespbd.database.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaction>()
    private lateinit var locationManager: LocationManager
    private lateinit var transactionRepository: TransactionRepository

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

        // Get the AppDatabase instance from the App class
        val appDatabase = (requireActivity().application as App).appDatabase
        // Get the TransactionDao from the AppDatabase
        val transactionDao = appDatabase.transactionDao()
        // Create the TransactionRepository
        transactionRepository = TransactionRepository(transactionDao)

        binding.addTransactionButton.setOnClickListener {
            navigateToAddTransactionFragment()
        }

        CoroutineScope(Dispatchers.IO).launch {
            getAllTransactions()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToAddTransactionFragment() {
        // Use Navigation Component to navigate to AddTransactionFragment
        findNavController().navigate(R.id.action_homeFragment_to_addTransactionFragment)
    }
    private suspend fun getAllTransactions() {
        transactions.clear()
        transactions.addAll(transactionRepository.getAllTransactions())
        withContext(Dispatchers.Main) {
            transactionAdapter.notifyDataSetChanged()
        }
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