package com.example.securechatapp.data.model.api

data class ResponseError(
    val success: Boolean,
    val message: String,
    val code: String
){
    companion object{
        const val REFRESH_TOKEN_EXPIRED = "refreshTokenExpired"
        const val REFRESH_TOKEN_INVALID  = "refreshTokenInvalid"
        const val ACCESS_TOKEN_EXPIRED = "accessTokenExpired"

    }
}