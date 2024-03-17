package com.example.tubespbd.ui.settings

import android.app.Activity
import android.net.Uri
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tubespbd.App
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.auth.LoginService
import com.example.tubespbd.databinding.FragmentSettingsBinding
import com.example.tubespbd.utils.MailService
import com.example.tubespbd.utils.SaveExcelService

class SettingsFragment : Fragment() {
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>
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
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            saveButton.setOnClickListener {
                settingsViewModel.saveToExcel(requireActivity())
            }

            sendButton.setOnClickListener {
                settingsViewModel.prepareEmail(requireActivity(), this@SettingsFragment)
            }

            logoutButton.setOnClickListener {
                val loginService = LoginService(requireContext())
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
}