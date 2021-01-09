package com.example.mychat.model

import android.util.Log
import com.example.mychat.model.db.DBService
import com.example.mychat.model.entity.Message
import com.example.mychat.model.entity.PushMessage
import com.example.mychat.model.message_api.MessageService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageService(private val dbService: DBService) {

    private val firebase = FirebaseMessaging.getInstance()
    var login = "Vasia Pupkin"

    fun syncMessagesFromDB() = dbService.getData().map {
        it.map { message ->
            val from = if (message.from != login) 1 else 2
            Pair(from, Message(message.message,"1", "2", message.title))
        }
    }

    fun getMessage(message: RemoteMessage) = CoroutineScope(Dispatchers.IO).launch {
        if (message.data["sender"] != login) dbService.insertData(message)
        Log.d(TAG, "---GET NEW MESSAGE---")
    }

    fun sendMessage(message: String, recipient: String, yourNikeName: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val pushMessage = PushMessage(Message( message,recipient = login,sender = login, yourNikeName), login)
            try {
                val response = MessageService().getAPI().postMessage(pushMessage)
                if (response.isSuccessful) {
                    Log.d(TAG, "Post message successful")
                    dbService.insertData(login, yourNikeName, message, login)
                } else Log.e(TAG, response.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }

    fun getToken() {
        firebase.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            login = task.result ?: "Empty"
            Log.d(TAG, "token: $login")
        })
    }

    companion object {
        const val TAG = "FirebaseService"
    }
}