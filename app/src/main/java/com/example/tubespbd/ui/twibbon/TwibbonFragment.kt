package com.example.tubespbd.ui.twibbon

import CameraHandler
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.R
import com.example.tubespbd.databinding.FragmentScanBinding
import com.example.tubespbd.databinding.FragmentTwibbonBinding
import com.example.tubespbd.responses.ItemResponse
import com.example.tubespbd.ui.NoConnectionActivity
import com.example.tubespbd.ui.scan.ScanFragment
import java.io.File
import java.io.FileOutputStream

class TwibbonFragment : Fragment() {

    private var _binding: FragmentTwibbonBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraHandler: CameraHandler<FragmentTwibbonBinding>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false).also {
            cameraHandler = CameraHandler(requireContext(), viewLifecycleOwner, it)
        }
        binding.captureButton.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.rounded_corner_button_disabled)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()) ActivityCompat.requestPermissions(requireActivity(),
            REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
        binding.captureButton.setOnClickListener {
            Log.e("ScanFragment", "Button clicked")
            cameraHandler.takePicture { imageUri ->
                Log.e("Scan Fragment", imageUri.toString())
                navigateToShowTwibbon(imageUri)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            cameraHandler.startCamera()
            binding.captureButton.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.rounded_corner_button_green)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            cameraHandler.startCamera();
            binding.captureButton.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.rounded_corner_button_green)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToShowTwibbon(imageUri: Uri) {
        try {
            findNavController().navigate(
                R.id.action_twibbonFragment_to_showTwibbonFragment,
                Bundle().apply {
                    putString("savedURIPhoto", imageUri.toString())
                })
        } catch (e: Exception) {
            Log.e("ScanFragment", "Navigation failed", e)
        }
    }

    private fun navigateToNoConnection() {
        val intent = Intent(requireContext(), NoConnectionActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
