package com.example.proyekinotekai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 2) // Versi dinaikkan menjadi 2
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
                .fallbackToDestructiveMigration() // Tambahkan ini
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}