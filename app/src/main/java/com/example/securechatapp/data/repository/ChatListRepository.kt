package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.datasources.RetrofitDataSource
import com.example.securechatapp.data.datasources.RoomDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

class ChatListRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {
    suspend fun getRoomList(uid: String) = roomDataSource.getRoomList(uid)

    suspend fun getRoomByID(uid: String, roomID: String) = roomDataSource.getRoomById(uid, roomID)

    fun getRoomListA(uid: String) = API.apiService.getRoomList2(uid)
}