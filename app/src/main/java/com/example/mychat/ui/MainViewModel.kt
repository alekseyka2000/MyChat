package com.example.mychat.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.model.MessageReceiver
import com.example.mychat.model.entity.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val messageReceiver: MessageReceiver) : ViewModel() {

    private val mutableLiveData = MutableLiveData<List<Pair<Int, Message>>>()
    val liveData: LiveData<List<Pair<Int, Message>>> = mutableLiveData

    fun sync() {
        CoroutineScope(Dispatchers.IO).launch {
                messageReceiver.getToken()
                messageReceiver.syncMessagesFromDB()
                    .catch { exception -> Log.d(TAG, exception.message.toString()) }
                    .collect { withContext(Dispatchers.Main) { mutableLiveData.value = it }}
        }
    }

    fun sendMessage(message: String, recipient: String, yourNikeName: String) =
        messageReceiver.sendMessage(message, recipient, yourNikeName)

    companion object {
        const val TAG = "FirebaseService"
    }
}