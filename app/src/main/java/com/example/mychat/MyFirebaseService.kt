package com.example.mychat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token :: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO : send token to tour server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        MessageReceiver.addMessage(message)
    }

    companion object{
        const val TAG = "FirebaseService "
    }
}
