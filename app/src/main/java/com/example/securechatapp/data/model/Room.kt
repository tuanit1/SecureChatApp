package com.example.securechatapp.data.model

import com.example.securechatapp.extension.decodeBase64
import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("_id")
    var id: String,
    var name: String,
    @SerializedName("image_ic")
    var image: String,
    var type: String,
) {
    companion object {
        const val PRIVATE = "private"
        const val GROUP = "group"
    }
}