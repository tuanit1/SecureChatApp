package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class ChatListRepository {
    fun getRoomList(uid: String, type: String) = API.apiService.getRoomList(uid, type)
}