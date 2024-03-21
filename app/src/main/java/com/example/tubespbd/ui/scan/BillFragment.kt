package com.example.tubespbd.ui.scan

import CameraHandler
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.R
import com.example.tubespbd.databinding.FragmentScanBinding
import com.example.tubespbd.databinding.FragmentShowBillBinding
import com.example.tubespbd.responses.ItemResponse
import java.io.File

class BillFragment : Fragment(){
    private var _binding: FragmentShowBillBinding? = null
    private val binding get() = _binding!!

    private var itemResponse: ItemResponse? = null
    private val imageUriString = arguments?.getString("imageUri")
    private val imageUri = imageUriString?.let { Uri.parse(it) }

    // On Create Function
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val billViewModel =
            ViewModelProvider(this)[BillViewModel::class.java]
        _binding = FragmentShowBillBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUriString = arguments?.getString("savedURI")
        val imageConvertedUri = imageUriString?.let { Uri.parse(it) }
        Log.e("bill", "Image URI: $imageConvertedUri")
        imageConvertedUri?.let {
            displayImage(it)
        }

        with(binding) {
            saveButton.setOnClickListener {
                navigateToHistoryFragment()
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

    private fun navigateToHistoryFragment(){
        findNavController().navigate(R.id.action_showBillFragment_to_homeFragment)
    }

    private fun navigateToScanFragment(){
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}