package com.example.securechatapp.data.datasources

import com.example.securechatapp.data.api.API
import javax.inject.Inject

class RoomDataSource @Inject constructor(): RetrofitDataSource() {
    suspend fun getPrivateRoom(currentUID: String, otherUID: String) = API.apiService.getPrivateRoom(currentUID, otherUID)

    suspend fun getRoomList(uid: String) = safeCallApi {
        API.apiService.getRoomList(uid)
    }

    suspend fun getRoomById(uid: String, roomID: String) = safeCallApi {
        API.apiService.getRoomByID(uid, roomID)
    }
}