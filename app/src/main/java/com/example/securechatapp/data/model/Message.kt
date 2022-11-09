package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class Message(

    @SerializedName("_id")
    var id: String,
    var message: String,
    var time: String,
    var type: String,
    @SerializedName("user_id")
    var uid: String,
    @SerializedName("room_id")
    var roomID: String,
){
    companion object{
        const val TEXT = "text"
    }
}