package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class UserRepository {
    fun getAllUser() = API.apiService.getAllUser()
    fun getUserByID(uid: String) = API.apiService.getUserByID(uid)
}