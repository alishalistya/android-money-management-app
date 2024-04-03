package com.example.tubespbd.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tubespbd.R
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionAdapter
import com.example.tubespbd.databinding.FragmentHistoryBinding
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.tubespbd.TransactionManager
import android.location.LocationManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.App
import com.example.tubespbd.database.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var isEditButtonClicked = false
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
        transactionAdapter = TransactionAdapter(transactions) { transaction ->
            if (isEditButtonClicked) {
                Log.d("HomeFragment", "Selected Transaction ID: ${transaction.id}")
                navigateToEditTransactionFragment(transaction.id)
                isEditButtonClicked = false
            }
        }
        binding.transactionRecyclerView.adapter = transactionAdapter

        val appDatabase = (requireActivity().application as App).appDatabase
        val transactionDao = appDatabase.transactionDao()
        transactionRepository = TransactionRepository(transactionDao)

        binding.addTransactionButton.setOnClickListener {
            navigateToAddTransactionFragment()
        }

        binding.editTransactionButton.setOnClickListener {
            isEditButtonClicked = true
            Toast.makeText(context, "Please select a transaction to edit.", Toast.LENGTH_SHORT).show()
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
        findNavController().navigate(R.id.action_homeFragment_to_addTransactionFragment)
    }

    private fun navigateToEditTransactionFragment(transactionId: Int) {
        val bundle = Bundle().apply {
            putInt("transactionId", transactionId)
        }
        findNavController().navigate(R.id.action_homeFragment_to_editTransactionFragment, bundle)
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
                val transactionManager = TransactionManager(requireContext(), locationManager)
                transactionManager.getLocation().toString()
            }
            !hasLocationPermissions() -> {
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