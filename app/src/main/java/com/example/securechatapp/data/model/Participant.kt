package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class Participant(
    @SerializedName("_id")
    var id: String,
    @SerializedName("user")
    var user: User,
    @SerializedName("room_id")
    var roomID: String,
    var nickname: String,
    var timestamp: String,
    var isAdmin: Boolean,
    var allowSendMSG: Boolean,
    var allowSendFile: Boolean,
    var allowViewFile: Boolean
)
