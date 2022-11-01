package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    var uid: String,
    var name: String,
    var age: String,
    var phone: String,
    var image: String
)