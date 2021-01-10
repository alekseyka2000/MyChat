package com.example.mychat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.model.ContactsProvider
import com.example.mychat.model.entity.Contact
import com.example.mychat.model.YouContactHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartViewModel(private val contactsProvider: ContactsProvider) : ViewModel() {

    private val contactsMutableLiveData = MutableLiveData<List<Contact>>()
    val contactLiveData: LiveData<List<Contact>> = contactsMutableLiveData

    fun fetchContactsList() {
        CoroutineScope(Dispatchers.IO).launch {
            contactsProvider.fetchContactsList()
                .collect { withContext(Dispatchers.Main) {
                    contactsMutableLiveData.value = it
                    contactsProvider.contactList = it
                } }
        }
    }

    fun addNewContact(name: String, contact: String) {
        CoroutineScope(Dispatchers.IO).launch { contactsProvider.addContactToDB(name, contact) }
    }

    fun deleteAllContact() {
        CoroutineScope(Dispatchers.IO).launch { contactsProvider.deleteAllContacts() }
    }

    fun getYouContact() = YouContactHolder.youLogin
    fun checkContact(contact: String) = contactsProvider.checkContact(contact)
}