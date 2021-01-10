package com.example.mychat.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mychat.model.entity.Contact
import com.example.mychat.model.entity.MessageForDB
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDAO {
    @Query("select * from messages where sender = :recipient or sender = :you")
    fun getAll(recipient: String, you: String): Flow<List<MessageForDB>>

    @Query("select * from contacts")
    fun getContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: List<MessageForDB>)

    @Query("DELETE FROM messages")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertContact(contact: Contact)

    @Query("DELETE FROM contacts")
    fun deleteAllContacts()

}