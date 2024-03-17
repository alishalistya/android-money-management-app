package com.example.tubespbd.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class MailService {
    fun sendEmail(context: Context, recipient: String, subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(mIntent, "Choose Email Client"))
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
