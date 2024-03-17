package com.example.tubespbd.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

class MailIntentUtil {
    fun sendEmail(context: Context, recipient: String, subject: String, message: String, uri: Uri) {
        // Intent Initializer
        val mIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Chooser Email
        val chooser = Intent.createChooser(mIntent, "Choose Email Client")
        val resInfoList = context.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

}
