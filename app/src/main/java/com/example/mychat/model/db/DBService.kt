package com.example.mychat.model.db

import android.util.Log
import com.example.mychat.model.MessageService
import com.example.mychat.model.YouContactHolder
import com.example.mychat.model.entity.Contact
import com.example.mychat.model.entity.MessageForDB
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class DBService(private val db: MessageDB) {

    fun insertData(message: RemoteMessage){
        db.messageDAO().insert(
            listOf(
                MessageForDB(
                    id = UUID.randomUUID().toString(),
                    to = message.data["recipient"],
                    from = message.data["sender"],
                    title = message.data["title"],
                    message = message.data["message"]
                )
            )
        )
    }

    fun insertData(login: String, yourNikeName: String, message: String, recipient: String){
        db.messageDAO().insert(
            listOf(
                MessageForDB(
                    id = UUID.randomUUID().toString(),
                    to =  recipient,
                    from = login,
                    title = yourNikeName,
                    message = message
                )
            )
        )
    }

    fun getData(recipient: String) = db.messageDAO().getAll(recipient, YouContactHolder.youLogin)
    fun deleteData() = db.messageDAO().deleteAll()

    fun getContactList() = db.messageDAO().getContacts()
    fun insertContact(name: String, contact: String) { db.messageDAO().insertContact(Contact( UUID.randomUUID().toString(), name, contact)) }
    fun deleteAllContacts() { db.messageDAO().deleteAllContacts() }

}