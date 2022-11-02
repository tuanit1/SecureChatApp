package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class ChatListRepository {
    fun getRoomList(uid: String) = API.apiService.getRoomList(uid)
}