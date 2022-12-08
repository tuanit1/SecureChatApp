package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API
import retrofit2.http.Body

class ParticipantRepository {
    fun getRoomParticipants(roomID: String) = API.apiService.getParticipantByRoomID(roomID)
    suspend fun getParticipantByRoomIDSuspend(roomID: String) = API.apiService.getParticipantByRoomIDSuspend(roomID)

    fun updateParticipant(id: String, body: HashMap<String, String>) = API.apiService.updateParticipant(id, body)
}