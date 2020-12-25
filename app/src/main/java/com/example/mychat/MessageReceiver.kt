package com.example.mychat

import android.util.Log
import com.google.firebase.messaging.RemoteMessage

object MessageReceiver {

    private val listMessages = mutableListOf<String>()

    fun addMessage(message: RemoteMessage){
        listMessages.add(message.notification?.body ?: "Empty")
        Log.d(MyFirebaseService.TAG, "Message :: -------------")
        Log.d(MyFirebaseService.TAG, "Message6 :: ${message.notification?.title}")
        Log.d(MyFirebaseService.TAG, "Message5 :: ${message.notification?.body}")
    }
}