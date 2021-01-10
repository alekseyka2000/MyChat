package com.example.mychat.model

import com.example.mychat.model.db.DBService

class ContactsProvider(private val dbService: DBService)  {

    fun fetchContactsList() = dbService.getContactList()
    fun addContactToDB(name: String, contact: String) {  dbService.insertContact(name, contact)  }
    fun deleteAllContacts() { dbService.deleteAllContacts() }
}