package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.datasources.RoomDataSource
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {
    suspend fun createPrivateRoom(currentUID: String, otherUID: String) =
        API.apiService.createPrivateRoom(currentUID, otherUID)

    suspend fun getPrivateRoom(currentUID: String, otherUID: String) = API.apiService.getPrivateRoom(currentUID, otherUID)

    suspend fun getRoomByID(uid: String, roomID: String) = API.apiService.getRoomByID(uid, roomID)
    suspend fun getRoomList(uid: String) = roomDataSource.getRoomList(uid)
}