package com.example.mychat.di

import com.example.mychat.model.ContactsProvider
import com.example.mychat.model.MessageService
import com.example.mychat.model.db.DBService
import com.example.mychat.model.db.MessageDB
import com.example.mychat.ui.MainViewModel
import com.example.mychat.ui.StartViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { MessageDB.getDatabase(get())}
    single { DBService(db = get()) }
    single { MessageService(dbService = get()) }
    single { ContactsProvider(dbService = get()) }
}
val viewModelModule = module {
    viewModel { MainViewModel(messageService= get()) }
    viewModel { StartViewModel(contactsProvider= get()) }
}