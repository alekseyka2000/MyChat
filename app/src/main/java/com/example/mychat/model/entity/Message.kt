package com.example.mychat.model.entity

data class Message(
    val message: String?,
    val recipient: String?,
    val sender: String?,
    val title: String?
)
