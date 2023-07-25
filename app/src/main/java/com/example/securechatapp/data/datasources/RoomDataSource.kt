package com.example.securechatapp.data.datasources

import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.api.NetworkResult
import javax.inject.Inject

class RoomDataSource @Inject constructor(): RetrofitDataSource() {
    suspend fun getPrivateRoom(currentUID: String, otherUID: String) = API.apiService.getPrivateRoom(currentUID, otherUID)

    suspend fun getRoomList(uid: String) = handleApi {
        API.apiService.getRoomList(uid)
    }

    suspend fun getRoomById(uid: String, roomID: String) = handleApi {
        API.apiService.getRoomByID(uid, roomID)
    }
}