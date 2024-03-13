    package com.example.tubespbd.database

    import android.content.ContentValues
    import android.content.Context
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper
    import android.location.Location
    import android.location.LocationManager
    import android.Manifest
    import androidx.core.app.ActivityCompat

    import android.content.pm.PackageManager
    import java.text.SimpleDateFormat
    import java.util.*

    class MyDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        private val context: Context = context
        data class Transaction(
            val id: Int,
            val title: String,
            val category: String,
            val amount: Int,
            val location: String,
            val tanggal: String // Add tanggal attribute
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
            private const val COLUMN_TANGGAL = "tanggal" // Define the column name for tanggal
        }

        private val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITLE TEXT, $COLUMN_CATEGORY TEXT, $COLUMN_AMOUNT REAL, $COLUMN_LOCATION TEXT, $COLUMN_TANGGAL TEXT)" // Add COLUMN_TANGGAL to create table statement

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }

        fun insertTransaction(title: String, category: String, amount: Int, location: String): Long {
            val db = this.writableDatabase
            val tanggal = getCurrentDateTime()
            val values = ContentValues().apply {
                put(COLUMN_TITLE, title)
                put(COLUMN_CATEGORY, category)
                put(COLUMN_AMOUNT, amount)
                put(COLUMN_LOCATION, location)
                put(COLUMN_TANGGAL, tanggal)
            }
            return db.insert(TABLE_NAME, null, values)
        }

        // Function to get current date and time in a specific format
        private fun getCurrentDateTime(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
        private fun getUserLocation(locationManager: LocationManager): String {
            // Check if the location permission is granted
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Handle the case where location permission is not granted
                return "Location permission not granted"
            }

            // Location permission is granted, proceed with getting the location
            val location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            return if (location != null) {
                "${location.latitude}, ${location.longitude}"
            } else {
                "Location not available"
            }
        }


        fun getAllTransactions(): ArrayList<Transaction> {
            val transactionList = ArrayList<Transaction>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            cursor?.use {
                while (cursor.moveToNext()) {
                    val idIndex = cursor.getColumnIndex(COLUMN_ID)
                    val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                    val categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY)
                    val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                    val locationIndex = cursor.getColumnIndex(COLUMN_LOCATION)
                    val tanggalIndex = cursor.getColumnIndex(COLUMN_TANGGAL)
                    // Check if the column exists before accessing its index
                    if (idIndex != -1 && titleIndex != -1 && categoryIndex != -1 && amountIndex != -1 && locationIndex != -1) {
                        val id = cursor.getInt(idIndex)
                        val title = cursor.getString(titleIndex)
                        val category = cursor.getString(categoryIndex)
                        val amount = cursor.getInt(amountIndex)
                        val location = cursor.getString(locationIndex)
                        val tanggal = cursor.getString(tanggalIndex)
                        val transaction = Transaction(id, title, category, amount, location, tanggal)
                        transactionList.add(transaction)
                    } else {

                    }
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