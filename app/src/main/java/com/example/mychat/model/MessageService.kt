package com.example.mychat.model

import android.util.Log
import com.example.mychat.model.db.DBService
import com.example.mychat.model.entity.Message
import com.example.mychat.model.entity.PushMessage
import com.example.mychat.model.message_api.MessageService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageService(private val dbService: DBService) {

    fun syncMessagesFromDB(recipient: String) = dbService.getData(recipient).map {
        it.map { message ->
            val from = if (message.from != YouContactHolder.youLogin) 1 else 2
            Pair(from, Message(message.message,"1", "2", message.title))
        }
    }

    fun getMessage(message: RemoteMessage) = CoroutineScope(Dispatchers.IO).launch {
        if (message.data["sender"] != YouContactHolder.youLogin) dbService.insertData(message)
        Log.d(TAG, "---GET NEW MESSAGE---${message.data["title"]}    ${message.data["sender"]}")
    }

    fun sendMessage(message: String, yourNikeName: String, recipient: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val pushMessage = PushMessage(Message( message,recipient,sender = YouContactHolder.youLogin, yourNikeName), recipient)
            try {
                val response = MessageService().getAPI().postMessage(pushMessage)
                if (response.isSuccessful) {
                    Log.d(TAG, "Post message successful")
                    dbService.insertData(YouContactHolder.youLogin, yourNikeName, message, recipient)
                } else Log.e(TAG, response.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }

    fun deleteChat() = CoroutineScope(Dispatchers.IO).launch { dbService.deleteData()}

    companion object {
        const val TAG = "FirebaseService"
    }
}