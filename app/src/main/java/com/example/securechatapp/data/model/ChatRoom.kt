package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class ChatRoom(
    var room: Room?,
    var message: Message?,
    var participant: Participant?,
)