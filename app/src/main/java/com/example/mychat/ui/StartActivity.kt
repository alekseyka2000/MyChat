package com.example.mychat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mychat.R
import org.koin.android.viewmodel.ext.android.viewModel

class StartActivity : AppCompatActivity() {

    private val startViewModel: StartViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        startViewModel.fetchContactsList()

        startViewModel.contactLiveData.observe(this, {})
    }
}