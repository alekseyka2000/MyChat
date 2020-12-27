package com.example.mychat.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.model.db.MessageDB
import com.example.mychat.model.entity.Message
import com.example.mychat.model.entity.PushMessage
import com.example.mychat.model.message_api.MessageService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension

class MainViewModel(private val db: MessageDB) : ViewModel() {

    private val mutableLiveData = MutableLiveData<List<Pair<Int, Message>>>()
    val liveData: LiveData<List<Pair<Int, Message>>> = mutableLiveData
    private val firebase = FirebaseMessaging.getInstance()
    var login = "Vasia Pupkin"

    private val mutableLiveData1 = MutableLiveData<String>()
    val liveData1: LiveData<String> = mutableLiveData1

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

    @KoinApiExtension
    @SuppressLint("CheckResult")
    fun fetchMessages() {
        try {
            val messageList = db.MessageDao().getAll()
            val listForView = mutableListOf<Pair<Int, Message>>()
            messageList.forEach { message ->
                val from = if (message.from != login) 1 else 2
                if (message.title != null && message.message != null) listForView.add(Pair(from, Message(message.title, message.message)))
                Log.d(TAG, message.toString())
            }
            mutableLiveData.value = listForView
        } catch (ex: Exception) {
            Log.d(TAG, "${ex.message}")
        }
    }

    fun sendMessage(message: String) = CoroutineScope(Dispatchers.IO).launch {
        val pushMessage = PushMessage(Message("1", message), login)
        try {
            val response = MessageService().getAPI().postMessage(pushMessage)
            if (response.isSuccessful) {
                Log.d(TAG, "Post message successful")
                withContext(Main){ mutableLiveData1.value = message }
            }
            else Log.e(TAG, response.errorBody().toString())
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    companion object {
        const val TAG = "FirebaseService"
    }
}