package com.example.mychat.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageForDB(
    @PrimaryKey(autoGenerate = false) var id: String,
    @ColumnInfo(name = "recipient") val to: String?,
    @ColumnInfo(name = "sender") val from: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "message") val message: String?
)
