package com.example.mychat.model

import com.example.mychat.model.db.DBService

class ContactsProvider(private val dbService: DBService)  {

    fun fetchContactsList() = dbService.getContactList()
}