package com.example.mychat.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.model.MessageService
import com.example.mychat.model.entity.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val messageService: MessageService) : ViewModel() {

    private val mutableLiveData = MutableLiveData<List<Pair<Int, Message>>>()
    val liveData: LiveData<List<Pair<Int, Message>>> = mutableLiveData

    fun sync(recipient: String) {
        CoroutineScope(Dispatchers.IO).launch {
            messageService.syncMessagesFromDB(recipient)
                .catch { exception -> Log.d(TAG, exception.message.toString()) }
                .collect { withContext(Dispatchers.Main) { mutableLiveData.value = it } }
        }
    }

    fun sendMessage(message: String, yourNikeName:String, recipient: String) =
        messageService.sendMessage(message, yourNikeName, recipient)

    fun deleteChat() = messageService.deleteChat()

    companion object {
        const val TAG = "FirebaseService"
    }
}