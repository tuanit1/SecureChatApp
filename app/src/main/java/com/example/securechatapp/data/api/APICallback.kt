package com.example.securechatapp.data.api

interface APICallback {
    fun onStart()
    fun onSuccess(data: Any? = null)
    fun onError(t: Throwable? = null)
}