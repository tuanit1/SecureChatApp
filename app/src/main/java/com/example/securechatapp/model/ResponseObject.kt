package com.example.securechatapp.model

import com.google.gson.annotations.SerializedName

data class ResponseObject<T>(
    val data: T?,
    @SerializedName("message") val responseMessage: String?
)