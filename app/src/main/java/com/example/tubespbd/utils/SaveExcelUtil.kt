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
import org.apache.commons.lang3.StringUtils.lowerCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.reflect.full.memberProperties

class SaveExcelUtil(private val context: Context) {
    fun createExcelDocument(transactions: List<Transaction>, fileName: String = "transaksi", fileType: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val mimeType = when (lowerCase(fileType)) {
                    "xls" -> "application/vnd.ms-excel"
                    "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    else -> throw IllegalArgumentException("Unsupported file type: $fileType")
                }
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.$fileType")
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    values
                ) ?: throw Exception("Failed to create new MediaStore record.")
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    generateExcelFile(outputStream, transactions, fileType)
                }
                Toast.makeText(context, "File created successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Fail to create file: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun generateExcelFile(
        outputStream: OutputStream,
        transactions: List<Transaction>,
        fileType: String
    ) {
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val workbook = if (fileType == "xls") {
                    HSSFWorkbook()
                } else {
                    XSSFWorkbook()
                }

                // Ensure workbook is not null before proceeding
                workbook.let { wb ->
                    try {
                        val sheet = wb.createSheet("Transactions")

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
                                    is Int -> row.createCell(colIndex)
                                        .setCellValue(value.toDouble())

                                    is Float? -> row.createCell(colIndex)
                                        .setCellValue(value?.toDouble() ?: 0.0)

                                    else -> row.createCell(colIndex).setCellValue(value?.toString())
                                }
                            }
                        }
                        wb.write(outputStream)
                        wb.close()
                        Log.e("ExcelGeneration", "Saved Excel tp: ${fileType}")

                    } catch (e: Exception) {
                        Log.e("ExcelGeneration", "Fail to create file: ${e.message}")
                    }
                }
            }
        }
    }

        suspend fun generateDocument(
            transactions: List<Transaction>,
            fileName: String = "transaksi",
            fileType : String = "xlsx"
        ): File {
            return withContext(Dispatchers.IO) {
                val directoryPath = context.filesDir.path
                val fileDocumentName = "$fileName.$fileType"
                val file = File(directoryPath, fileDocumentName)
                FileOutputStream(file).use { outputStream ->
                    generateExcelFile(outputStream, transactions, fileType)
                }
                file
            }
        }
    }



