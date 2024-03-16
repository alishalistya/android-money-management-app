package com.example.tubespbd.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tubespbd.LoginActivity
import com.example.tubespbd.R
import com.example.tubespbd.auth.LoginService
import com.example.tubespbd.auth.TokenManager
import com.example.tubespbd.databinding.FragmentSettingsBinding
import com.example.tubespbd.email.MailService

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.sendButton.setOnClickListener {
            val recipient = "alisha.listya@gmail.com"
            val subject = "Subject of the Email"
            val message = "Message body of the email."

            val mailIntent = MailService()
            mailIntent.sendEmail(requireContext(), recipient, subject, message)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            // delete token
            TokenManager.clearToken()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}