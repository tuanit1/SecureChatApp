package com.example.securechatapp

import android.app.Application
import com.example.securechatapp.data.api.API
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        API.initAPIService(this)
    }
}