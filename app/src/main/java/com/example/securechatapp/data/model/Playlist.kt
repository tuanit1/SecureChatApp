package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class Playlist(
    @SerializedName("playlist_id")
    val id: Int,
    @SerializedName("playlist_name")
    val name: String,
    @SerializedName("playlist_thumb")
    val thumbnail: String
)