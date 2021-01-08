package com.example.mychat.model

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class MyFirebaseService : FirebaseMessagingService() {

    private val messageReceiver by inject<MessageReceiver>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token :: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        messageReceiver.getMessage(message)
    }


    companion object {
        const val TAG = "FirebaseService "
    }
}
