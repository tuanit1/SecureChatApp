package com.example.securechatapp.data.model

import android.os.Message
import com.google.gson.annotations.SerializedName

data class ResponseObject<T>(
    val data: T?,
    val success: Boolean,
    @SerializedName("message") val responseMessage: String?
)