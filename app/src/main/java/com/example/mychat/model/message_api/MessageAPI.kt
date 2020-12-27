package com.example.mychat.model.message_api

import com.example.mychat.Constants.Companion.CONTENT_TYPE
import com.example.mychat.Constants.Companion.SERVER_KEY
import com.example.mychat.model.entity.PushMessage
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MessageAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postMessage(
        @Body message: PushMessage
    ): Response<ResponseBody>
}