package com.example.mychat.model

import android.util.Log
import com.google.firebase.messaging.RemoteMessage


object MessageReceiver {

    private val listMessages = mutableListOf<String>()

    fun addMessage(message: RemoteMessage){
        listMessages.add(message.notification?.body ?: "Empty")
        Log.d(MyFirebaseService.TAG, "---GET---")
        Log.d(MyFirebaseService.TAG, "Title :: ${message.data["title"]}")
        Log.d(MyFirebaseService.TAG, "Message :: ${message.data["message"]}")
    }
}