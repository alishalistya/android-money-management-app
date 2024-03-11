package com.example.tubespbd.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Transaction(
        val id: Int,
        val title: String,
        val category: String,
        val amount: Int,
        val location: String
    )

    companion object {
        private const val DATABASE_NAME = "transaction.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_LOCATION = "location"
    }

    private val CREATE_TABLE =
        "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, $COLUMN_CATEGORY TEXT, $COLUMN_AMOUNT REAL, $COLUMN_LOCATION TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTransaction(title: String, category: String, amount: Int, location: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_LOCATION, location)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllTransactions(): ArrayList<Transaction> {
        val transactionList = ArrayList<Transaction>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                val amount = cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT))
                val location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION))
                val transaction = Transaction(id, title, category, amount, location)
                transactionList.add(transaction)
            }
        }
        return transactionList
    }

    fun updateTransaction(id: Int, title: String, category: String, amount: Int, location: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_LOCATION, location)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteTransaction(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}
