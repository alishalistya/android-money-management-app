package com.example.tubespbd.ui.scan

import CameraHandler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.App
import com.example.tubespbd.R
import com.example.tubespbd.billuploads.BillService
import com.example.tubespbd.database.Transaction
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.databinding.FragmentScanBinding
import com.example.tubespbd.databinding.FragmentShowBillBinding
import com.example.tubespbd.responses.ItemResponse
import com.example.tubespbd.ui.NoConnectionActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BillFragment : Fragment(){
    private var _binding: FragmentShowBillBinding? = null
    private lateinit var cameraHandler: CameraHandler
    private val binding get() = _binding!!
    private val billViewModel: BillViewModel by viewModels{
        BillViewModelFactory((requireActivity().application as App).transactionRepository)
    }
    private var itemResponse: ItemResponse? = null
    private lateinit var imageUriString : String

    // On Create Function
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        transactionRepository = TransactionRepository()
        _binding = FragmentShowBillBinding.inflate(inflater, container, false)
        imageUriString = arguments?.getString("savedURI").toString()
        Log.e("Scan 1:", "URI: $imageUriString")
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUriString = arguments?.getString("savedURI").toString()
        val imageConvertedUri = imageUriString?.let { Uri.parse(it) }
        Log.e("bill", "Image URI: $imageConvertedUri")
        imageConvertedUri?.let {
            displayImage(it)
        }

        with(binding) {
            saveButton.setOnClickListener {
                attemptPost()
            }
            recaptureButton.setOnClickListener {
                navigateToScanFragment()
            }
        }
    }

    private fun displayImage(imageUri: Uri) {
        // Ensure you have an ImageView in your layout to display the image.
        Log.e("e", "URI: $imageUri")
        binding.imageView.setImageURI(imageUri)
    }

    private fun saveTransaction(itemResponse: ItemResponse){
        itemResponse.let {
            it.items.items.forEach{ bill ->
                val transaction = Transaction(
                    category = "Pengeluaran",
                    amount = bill.price.toFloat(),
                    tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    title = bill.name,
                    location = "Contoh location"
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    billViewModel.insertTransactions(transaction)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun attemptPost() {
        imageUriString = arguments?.getString("savedURI").toString()
        GlobalScope.launch {
            Log.e("Scan 2:", "URI: $imageUriString")
            val fileUri = Uri.parse(imageUriString)
            val file = fileUri?.path?.let { File(it) }

            val billService = BillService()
            val response = context?.let {
                if (file != null) {
                    itemResponse = billService.postBill(it, file)!!
                }
            }
            if (itemResponse != null) {
                Log.d("Response", "Post bill success! $itemResponse")
                // Saved in itemResponse attribute
                saveTransaction(itemResponse!!)
                withContext(Dispatchers.Main) {
                    // This block is now executed on the main thread.
                    navigateToHistoryFragment()
                }
            } else {
                navigateToNoConnection()
            }
        }
    }

    private fun navigateToHistoryFragment(){
        findNavController().navigate(R.id.action_showBillFragment_to_homeFragment)
    }

    private fun navigateToScanFragment(){
        findNavController().navigateUp()
    }

    private fun navigateToNoConnection() {
        val intent = Intent(requireContext(), NoConnectionActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}