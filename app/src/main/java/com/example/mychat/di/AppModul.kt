package com.example.mychat.di

import com.example.mychat.model.MessageService
import com.example.mychat.model.db.DBService
import com.example.mychat.model.db.MessageDB
import com.example.mychat.ui.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {

    single { MessageDB.getDatabase(get())}
    viewModel { MainViewModel(messageService= get()) }
    single { DBService(db = get()) }
    single { MessageService(dbService = get()) }
}