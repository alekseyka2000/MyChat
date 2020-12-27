package com.example.myweather.model.db_service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mychat.model.entity.MessageForDB

@Dao
interface MessageDAO {
    @Query("select * from messages")
    fun getAll(): List<MessageForDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: List<MessageForDB>)

    @Query("DELETE FROM messages")
    fun deleteAll()

}