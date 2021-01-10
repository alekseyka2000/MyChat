package com.example.mychat.model

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class MyFirebaseService : FirebaseMessagingService() {

    private val messageService by inject<MessageService>()
    private val contactsProvider by inject<ContactsProvider>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        YouContactHolder.youLogin = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val sender = message.data["sender"]
        sender?.let {
            if (!contactsProvider.checkContact(sender))
                contactsProvider.addContactToDB(
                    message.data["title"] ?: "",
                    sender
                )
        }
        messageService.getMessage(message)
    }
}

