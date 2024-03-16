package com.example.tubespbd.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.App
import com.example.tubespbd.R
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.databinding.FragmentAddTransactionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.tubespbd.database.Transaction
import java.util.Date


class EditTransactionFragment : Fragment() {

    private lateinit var transactionRepository: TransactionRepository
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        val appDatabase = (requireActivity().application as App).appDatabase
        val transactionDao = appDatabase.transactionDao()
        transactionRepository = TransactionRepository(transactionDao)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getInt("transactionId")

        if (transactionId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val transaction = transactionRepository.getTransactionById(transactionId)
                withContext(Dispatchers.Main) {
                    binding.titleEditText.setText(transaction?.title)
                    binding.categoryEditText.setText(transaction?.category)
                    binding.amountEditText.setText(transaction?.amount.toString())
                }
            }
        }

        // Buat Delete button visible, karena pake fragment yang sama dengan addTransaction
        binding.deleteTransactionButton.visibility = View.VISIBLE

        // Delete Button
        binding.deleteTransactionButton.setOnClickListener {
            val transactionId = arguments?.getInt("transactionId")
            if (transactionId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    transactionRepository.deleteTransactionById(transactionId)
                    withContext(Dispatchers.Main) {
                        navigateBack()
                    }
                }
            }
        }

        // Reuse button addTransaction untuk Update
        binding.addTransactionButton.setOnClickListener {
            val transactionId = arguments?.getInt("transactionId")
            Log.d("EditTransactionFragment", "Transaction ID: $transactionId")
            if (transactionId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    updateTransaction(transactionId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Transaksi Berhasil Di-update", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            navigateBack()
        }

        val appDatabase = (requireActivity().application as App).appDatabase
        val transactionDao = appDatabase.transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateTransaction(transactionId: Int) {
        val title = binding.titleEditText.text.toString()
        val category = binding.categoryEditText.text.toString()
        val amount = binding.amountEditText.text.toString().toFloat()
        val locationString = "Location unavailable" // HARDCODE TEMP
        val currentDate = Date()

        val updatedTransaction = Transaction(
            id = transactionId,
            title = title,
            category = category,
            amount = amount,
            location = locationString,
            tanggal = currentDate.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.updateTransaction(updatedTransaction)
            withContext(Dispatchers.Main) {
                binding.titleEditText.text.clear()
                binding.categoryEditText.text.clear()
                binding.amountEditText.text.clear()
            }
        }
    }

}