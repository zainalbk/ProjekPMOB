package com.example.proyekinotekai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 3, exportSchema = false) // Versi dinaikkan menjadi 3
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "inotekai.db"
                )
                .fallbackToDestructiveMigration() // Menghancurkan dan membuat ulang database jika skema berubah
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}