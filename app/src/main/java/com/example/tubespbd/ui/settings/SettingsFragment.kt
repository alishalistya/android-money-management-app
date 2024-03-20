package com.example.tubespbd.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tubespbd.App
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.R
import com.example.tubespbd.auth.LoginService
import com.example.tubespbd.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
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
//                settingsViewModel.saveToExcel(requireActivity()) {
                // Initializing the popup menu and giving the reference as current context
                val popupMenu = PopupMenu(activity, saveButton)

                // Inflating popup menu from popup_menu.xml file
                popupMenu.menuInflater.inflate(R.menu.excel_popup_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.export_to_xls -> {
                            settingsViewModel.saveToExcel(requireActivity(), "xls")
                        }

                        R.id.export_to_xlsx -> {
                            settingsViewModel.saveToExcel(requireActivity(), "xlsx")
                        }
                    }
                    Toast.makeText(activity, "You Clicked ${menuItem.title}", Toast.LENGTH_SHORT).show()
                    true}
                // Showing the popup menu
                popupMenu.show()
            }

            sendButton.setOnClickListener {
                settingsViewModel.prepareEmail(requireActivity(), this@SettingsFragment)

            }

            sendBcButton.setOnClickListener {
                context?.sendBroadcast(Intent("RANDOMIZE"))
                Log.i("BC", "Sent Broadcast")
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