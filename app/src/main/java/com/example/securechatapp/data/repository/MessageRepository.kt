package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class MessageRepository {
    suspend fun getMessagesByRoomID(
        roomUID: String,
        page: Int,
        step: Int
    ) = API.apiService.getMessagesByRoomID(roomUID, page, step)

    fun createMessage(
        uid: String,
        roomUID: String,
        body: HashMap<String, String>
    ) = API.apiService.createMessage(uid, roomUID, body)

}