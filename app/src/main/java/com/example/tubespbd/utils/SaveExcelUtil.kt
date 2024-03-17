package com.example.tubespbd.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.tubespbd.database.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.reflect.full.memberProperties

class SaveExcelUtil(private val context: Context) {
    fun createExcelDocument(transactions: List<Transaction>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "transaksi.xlsx")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    values
                ) ?: throw Exception("Failed to create new MediaStore record.")
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    generateExcelFile(outputStream, transactions)
                }
                Toast.makeText(context, "File created successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Fail to create file: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun generateExcelFile(outputStream: OutputStream, transactions: List<Transaction>) {
        withContext(Dispatchers.IO) {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transactions")

            val headerRow = sheet.createRow(0)
            Transaction::class.memberProperties.forEachIndexed { index, kProperty ->
                headerRow.createCell(index).setCellValue(kProperty.name)
            }

            transactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                Log.d("[TEST] Done creating: ", "index: $index")
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
            workbook.write(outputStream)
            workbook.close()
        }
    }

    suspend fun generateDocument(transactions: List<Transaction>):File{
        return withContext(Dispatchers.IO) {
            val directoryPath = context.filesDir.path
            val fileName = "transactions.xlsx"
            val file = File(directoryPath, fileName)
            FileOutputStream(file).use { outputStream ->
                generateExcelFile(outputStream, transactions)
            }
            file
        }
    }
}


