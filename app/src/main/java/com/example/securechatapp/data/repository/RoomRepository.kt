package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API

class RoomRepository {
    suspend fun createPrivateRoom(currentUID: String, otherUID: String) = API.apiService.createPrivateRoom(currentUID, otherUID)
    suspend fun getPrivateRoom(currentUID: String, otherUID: String) = API.apiService.getPrivateRoom(currentUID, otherUID)
}