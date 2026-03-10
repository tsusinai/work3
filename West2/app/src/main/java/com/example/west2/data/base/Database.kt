package com.example.west2.data.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.west2.data.dao.JournalDao
import com.example.west2.data.dao.JournalImageDao
import com.example.west2.data.model.Journal
import com.example.west2.data.model.JournalImage

@Database(
    entities = [Journal::class, JournalImage::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun journalImageDao(): JournalImageDao

    companion object {
        // 单例模式
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "journal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}