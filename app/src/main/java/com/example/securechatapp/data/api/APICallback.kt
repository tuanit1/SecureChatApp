package com.example.securechatapp.data.api

interface APICallback {
    fun onStart()
    fun onSuccess()
    fun onError(t: Throwable? = null)
}