package com.example.tubespbd.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tubespbd.App
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.auth.LoginService
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.databinding.FragmentSettingsBinding
import com.example.tubespbd.utils.MailService

class SettingsFragment : Fragment() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory((requireActivity().application as App).transactionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        settingsViewModel.allTransactions.observe(viewLifecycleOwner){
            Log.d("SettingsFragment", "get all transactions")
            for (transactions in it) {
                Log.d("SettingsFragment", "all transactions: $transactions")
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize permission request launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                settingsViewModel.saveToExcel()
                Toast.makeText(context, "Permissions granted. Saving to Excel...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
        setupUI()
    }

    fun setupUI() {
        with(binding) {
            saveButton.setOnClickListener {
                if (allPermissionsGranted()) {
                    settingsViewModel.saveToExcel()
                    Log.d("Settings Fragment", "Saved transactions to Excel.")
                } else {
                    requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_STORAGE)
                }
            }

            sendButton.setOnClickListener {
                val recipient = "alisha.listya@gmail.com"
                val subject = "Subject of the Email"
                val message = "Message body of the email."

                val mailIntent = MailService()
                mailIntent.sendEmail(requireContext(), recipient, subject, message)
            }

            logoutButton.setOnClickListener {
                val loginService = LoginService()
                loginService.logout()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS_STORAGE.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}