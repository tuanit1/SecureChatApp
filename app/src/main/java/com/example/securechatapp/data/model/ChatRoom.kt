package com.example.securechatapp.data.model

data class ChatRoom(
    var room: Room,
    var messages: List<Message>,
    var user: User,
)