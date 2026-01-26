package com.nihao.ai_adventurer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nihao.ai_adventurer.database.dao.DialogDao
import com.nihao.ai_adventurer.database.dao.DialogMessageDao
import com.nihao.ai_adventurer.database.entities.Dialog
import com.nihao.ai_adventurer.database.entities.DialogMessage

@Database(
    entities = [Dialog::class, DialogMessage::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dialogDao(): DialogDao
    abstract fun dialogMessageDao(): DialogMessageDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_adventurer_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
