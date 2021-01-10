package com.example.mychat.model

import com.example.mychat.model.db.DBService
import com.example.mychat.model.entity.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsProvider(private val dbService: DBService) {

    var contactList = listOf<Contact>()

    fun fetchContactsList() = dbService.getContactList()
    fun addContactToDB(name: String, contact: String) {
        dbService.insertContact(name, contact)
    }

    fun deleteAllContacts() {
        dbService.deleteAllContacts()
    }

    fun checkContact(contact: String): Boolean {
        var result = false
        contactList.forEach { if(contact == it.contact) result = true }
        return result
    }
}