package com.example.tubespbd.ui.scan


import CameraHandler
import ImageSavedCallback
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tubespbd.databinding.FragmentScanBinding
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tubespbd.billuploads.BillService
import com.example.tubespbd.responses.ItemResponse
import com.example.tubespbd.ui.NoConnectionActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.io.File

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private lateinit var cameraHandler: CameraHandler

    private val binding get() = _binding!!

    private var itemResponse: ItemResponse? = null

    // On Create Function
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scanViewModel =
            ViewModelProvider(this)[ScanViewModel::class.java]
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        _binding?.let {
            cameraHandler = CameraHandler(requireContext(), viewLifecycleOwner, it)
        }
        val root: View = binding.root
//        val textView: TextView = binding.textScan
        scanViewModel.text.observe(viewLifecycleOwner){
//            textView.text = it
        }
        return root
    }

    // On View Created Function, for permission after view is made
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            cameraHandler.startCamera()
            binding.captureButton.setOnClickListener {
                cameraHandler.takePicture(object : ImageSavedCallback {
                    override fun onImageSaved(imageFile: File) {
                        // Image has been saved, now you can access it
                        attemptPost()
                    }
                })

            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraHandler.startCamera()
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun attemptPost() {
        GlobalScope.launch {
            val file = cameraHandler.getCapturedImageFile()

            val billService = BillService()
            val response = context?.let {
                if (file != null) {
                    itemResponse = billService.postBill(it, file)!!
                }
            }

            if (itemResponse != null) {
                Log.d("Response", "Post bill success! $itemResponse")
                // Saved in itemResponse attribute
            } else {
                navigateToNoConnection()
            }


        }
    }

    private fun navigateToNoConnection() {
        val intent = Intent(requireContext(), NoConnectionActivity::class.java)
        startActivity(intent)
    }
}