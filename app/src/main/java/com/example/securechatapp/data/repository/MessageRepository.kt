package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class MessageRepository {
    fun getMessagesByRoomID(roomUID: String) = API.apiService.getMessagesByRoomID(roomUID)
}