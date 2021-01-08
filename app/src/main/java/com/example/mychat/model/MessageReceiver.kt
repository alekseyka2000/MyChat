package com.example.mychat.model

import android.util.Log
import com.example.mychat.model.db.DBService
import com.example.mychat.model.entity.Message
import com.example.mychat.model.entity.PushMessage
import com.example.mychat.model.message_api.MessageService
import com.example.mychat.ui.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageReceiver(private val dbService: DBService) {

    private var listMessages = mutableListOf<Pair<Int, Message>>()
    private val firebase = FirebaseMessaging.getInstance()
    var login = "Vasia Pupkin"

    fun syncMessagesFromDB() = dbService.getData().map {
        it.map { message ->
            val from = if (message.from != login) 1 else 2
            Pair(from, Message(message.title, message.message))
        }
    }

    fun getMessage(message: RemoteMessage) = CoroutineScope(Dispatchers.IO).launch {
        if (message.data["from "] != login) {
            withContext(Dispatchers.Main) {
                listMessages.add(
                    Pair(
                        2, Message(
                            message.data["title"] ?: "Empty title",
                            message.data["message"] ?: "Empty message"
                        )
                    )
                )
                Log.d(TAG, "Add to DB")
            }
            dbService.insertData(message)
        }
        Log.d(TAG, "---GET---")
        Log.d(TAG, "Title :: ${message.data["title"]}")
        Log.d(TAG, "Message :: ${message.data["message"]}")
    }

    fun sendMessage(message: String, recipient: String, yourNikeName: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val pushMessage = PushMessage(Message(login, message), login)
            try {
                val response = MessageService().getAPI().postMessage(pushMessage)
                if (response.isSuccessful) {
                    Log.d(TAG, "Post message successful")
                    dbService.insertData(login, yourNikeName, message)
                    withContext(Dispatchers.Main) {
                        listMessages.add(
                            Pair(1, Message("you", message))
                        )
                    }
                } else Log.e(TAG, response.errorBody().toString())
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
            val msg = task.result ?: "Empty"
            login = msg
            Log.d(TAG, "token: $msg")
        })
    }

    companion object {
        const val TAG = "FirebaseService"
    }
}