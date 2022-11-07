package com.example.securechatapp.data.model

data class ChatRoom(
    var room: Room,
    var messages: MutableList<Message>,
    var user: User?,
)