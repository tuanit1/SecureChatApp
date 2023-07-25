package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API
import javax.inject.Inject

class UserRepository @Inject constructor() {
    fun getAllUser() = API.apiService.getAllUser()
    fun getUserByID(uid: String) = API.apiService.getUserByID(uid)
}