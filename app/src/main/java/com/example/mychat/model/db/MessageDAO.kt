package com.example.mychat.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mychat.model.entity.MessageForDB
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDAO {
    @Query("select * from messages")
    fun getAll(): Flow<List<MessageForDB>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: List<MessageForDB>)

    @Query("DELETE FROM messages")
    fun deleteAll()

}