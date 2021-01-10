package com.example.mychat.model.message_api

import android.util.Log
import com.example.mychat.model.MessageService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object YouContactHolder {

    var youLogin = "Empty"
    private val firebase = FirebaseMessaging.getInstance()

    fun getToken() {
        firebase.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(MessageService.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            youLogin = task.result ?: "Empty"
            Log.d(MessageService.TAG, "token: $youLogin")
        })
    }
}