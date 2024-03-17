package com.example.tubespbd.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tubespbd.database.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.full.memberProperties


class SaveExcelService {
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    suspend fun generateExcelFile(transactions: List<Transaction>, filePath: String = root.absolutePath) {
        val file = File(filePath)
        if (!file.exists()) file.mkdirs()

        try {
            withContext(Dispatchers.IO) {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Transactions")

                val headerRow = sheet.createRow(0)
                Transaction::class.memberProperties.forEachIndexed { index, kProperty ->
                    headerRow.createCell(index).setCellValue(kProperty.name)
                }

                transactions.forEachIndexed { index, transaction ->
                    val row = sheet.createRow(index + 1)
                    Transaction::class.memberProperties.forEachIndexed { colIndex, kProperty ->
                        val value = kProperty.get(transaction)
                        when (value) {
                            is String? -> row.createCell(colIndex).setCellValue(value)
                            is Int -> row.createCell(colIndex).setCellValue(value.toDouble())
                            is Float? -> row.createCell(colIndex).setCellValue(value?.toDouble() ?: 0.0)
                            else -> row.createCell(colIndex).setCellValue(value?.toString())
                        }
                    }
                }

                // Write to file
                FileOutputStream(file).use {
                    workbook.write(it)
                }
                workbook.close()
            }
        } catch (e: Exception) {
            Log.e("SaveExcel", e.toString())
        }
    }

    fun checkPermission(activity: Activity, permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(activity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

}




