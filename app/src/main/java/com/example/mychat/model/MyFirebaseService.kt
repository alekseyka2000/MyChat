package com.example.mychat.model

import android.util.Log
import com.example.mychat.model.db.MessageDB
import com.example.mychat.model.entity.MessageForDB
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import java.util.*

class MyFirebaseService : FirebaseMessagingService() {

    val db by inject<MessageDB>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token :: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        db.MessageDao().insert(
            listOf(
                MessageForDB(
                    id = UUID.randomUUID().toString(),
                    to = message.to,
                    from = message.from,
                    title = message.data["title"],
                    message = message.data["message"]
                )
                )
            )
        MessageReceiver.addMessage(message)
    }


    companion object {
        const val TAG = "FirebaseService "
    }
}
