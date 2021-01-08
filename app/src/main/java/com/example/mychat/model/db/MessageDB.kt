package com.example.mychat.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mychat.model.entity.MessageForDB

@Database(entities = arrayOf(MessageForDB::class), version = 1, exportSchema = false)
abstract class MessageDB : RoomDatabase() {

    abstract fun messageDAO(): MessageDAO

    companion object {

        @Volatile
        private var INSTANCE: MessageDB? = null

        fun getDatabase(context: Context): MessageDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDB::class.java,
                    "forecast_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
