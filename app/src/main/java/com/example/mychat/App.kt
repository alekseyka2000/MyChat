package com.example.mychat

import android.app.Application
import com.example.mychat.di.repositoryModule
import com.example.mychat.di.viewModelModule
import com.example.mychat.model.YouContactHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        YouContactHolder.getToken()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(repositoryModule, viewModelModule)
        }
    }
}