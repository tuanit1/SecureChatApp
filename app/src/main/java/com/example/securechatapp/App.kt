package com.example.securechatapp

import android.app.Application
import com.example.securechatapp.data.api.API

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        API.initAPIService(this)
    }
}