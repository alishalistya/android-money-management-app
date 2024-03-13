package com.example.tubespbd.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tubespbd.databinding.FragmentScanBinding
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import com.google.common.util.concurrent.ListenableFuture
import androidx.core.content.ContextCompat

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scanViewModel =
            ViewModelProvider(this)[ScanViewModel::class.java]
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textScan
        scanViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
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

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .build()
        val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind use case
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview
            )
        } catch (exc: Exception) {
            Log.e("ScanFragment", "Use case binding failed", exc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}