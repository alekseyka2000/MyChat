package com.example.mychat.model.db

import com.example.mychat.model.entity.MessageForDB
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class DBService(private val db: MessageDB) {

    fun insertData(message: RemoteMessage){
        db.messageDAO().insert(
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
    }

    fun insertData(login: String, yourNikeName: String, message: String){
        db.messageDAO().insert(
            listOf(
                MessageForDB(
                    id = UUID.randomUUID().toString(),
                    to = login,// recipient,
                    from = login,
                    title = yourNikeName,
                    message = message
                )
            )
        )
    }

    fun getData() = db.messageDAO().getAll()
}