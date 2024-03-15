package com.example.tubespbd
import android.app.Application
import androidx.room.Room
import com.example.tubespbd.database.AppDatabase

class App : Application() {
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transaction.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}