package com.example.mychat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.model.ContactsProvider
import com.example.mychat.model.entity.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.compat.ScopeCompat

class StartViewModel(private val contactsProvider: ContactsProvider) : ViewModel() {

    private val contactsMutableLiveData = MutableLiveData<List<Contact>>()
    val contactLiveData: LiveData<List<Contact>> = contactsMutableLiveData

    fun fetchContactsList() {
        CoroutineScope(Dispatchers.IO).launch {
            contactsProvider.fetchContactsList()
                .collect { withContext(Dispatchers.Main) { contactsMutableLiveData.value = it } }
        }
    }
}