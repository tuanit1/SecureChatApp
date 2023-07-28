package com.example.securechatapp.data.model.api
import com.google.gson.annotations.SerializedName

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: ResponseObject<out T>): NetworkResult<T>()
    data class Error(val code: String, val message: String?): NetworkResult<Nothing>()
    data class Exception(val e: Throwable): NetworkResult<Nothing>()
}
open class ResponseObject<T>(
    val data: T? = null,
    val success: Boolean = false,
    @SerializedName("message") val responseMessage: String? = null
) {
    class GetRoomListData<T>: ResponseObject<T>()
    class GetRoomByIdData<T>: ResponseObject<T>()
}