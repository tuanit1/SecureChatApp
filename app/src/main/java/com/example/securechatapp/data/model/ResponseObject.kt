package com.example.securechatapp.data.model

import com.google.gson.annotations.SerializedName

data class ResponseObject<T>(
    val data: T?,
    @SerializedName("message") val responseMessage: String?
)