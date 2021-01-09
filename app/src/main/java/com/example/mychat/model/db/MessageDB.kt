package com.example.mychat.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mychat.model.entity.Contact
import com.example.mychat.model.entity.MessageForDB


@Database(entities = [MessageForDB::class, Contact::class], version = 2, exportSchema = true)
abstract class MessageDB : RoomDatabase() {

    abstract fun messageDAO(): MessageDAO

    companion object {

        @Volatile
        private var INSTANCE: MessageDB? = null

        private val migrationFrom1To2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE CONTACTS (id TEXT not null, name TEXT not null, contact TEXT not null, PRIMARY KEY (id))")
            }
        }

        fun getDatabase(context: Context): MessageDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDB::class.java,
                    "forecast_database"
                ).addMigrations(migrationFrom1To2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
