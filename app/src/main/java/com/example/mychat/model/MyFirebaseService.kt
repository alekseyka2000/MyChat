package com.example.mychat.model

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class MyFirebaseService : FirebaseMessagingService() {

    private val messageService by inject<MessageService>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        messageService.login = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        messageService.getMessage(message)
    }
}
