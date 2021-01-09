package com.example.mychat.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(name = "name") val nickname: String,
    @ColumnInfo(name = "contact") val contact: String
)
