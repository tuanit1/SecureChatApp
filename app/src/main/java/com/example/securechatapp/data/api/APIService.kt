package com.example.securechatapp.data.api

import com.example.securechatapp.data.model.*
import com.example.securechatapp.data.model.api.AuthToken
import com.example.securechatapp.data.model.api.ResponseObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    //AUTH
    @POST("auth/login/{uid}")
    fun getAuthToken(@Path("uid") uid: String): Call<ResponseObject<AuthToken>>

    @PUT("auth/logout")
    suspend fun logoutToken()

    @POST("auth/token")
    suspend fun refreshToken(@Body body: HashMap<String, String>): Response<ResponseObject<AuthToken>>

    //ROOM
    @GET("room/{uid}")
    @JvmSuppressWildcards
    suspend fun getRoomList(@Path("uid") uid: String): Response<ResponseObject.GetRoomListData<MutableList<ChatRoom>>>

    @GET("room/{uid}")
    @JvmSuppressWildcards
    fun getRoomList2(@Path("uid") uid: String): Call<ResponseObject<MutableList<ChatRoom>>>

    @POST("room/{uid}")
    fun addRoom(
        @Path("uid") uid: String,
        @Body body: HashMap<String, String>
    ): Call<ResponseObject<Room>>

    @POST("room/private/{currentUID}&{otherUID}")
    suspend fun createPrivateRoom(@Path("currentUID") currentUID: String, @Path("otherUID") otherUID: String): ResponseObject<Room>

    @GET("room/check/{currentUID}&{otherUID}")
    suspend fun getPrivateRoom(@Path("currentUID") currentUID: String, @Path("otherUID") otherUID: String): ResponseObject<ChatRoom>

    @GET("room/{uid}&{roomID}")
    suspend fun getRoomByID(@Path("uid") uid: String, @Path("roomID") roomID: String): Response<ResponseObject.GetRoomByIdData<ChatRoom>>

    //USER
    @GET("user")
    fun getAllUser(): Call<ResponseObject<MutableList<User>>>

    @GET("user/{uid}")
    fun getUserByID(@Path("uid") uid: String): Call<ResponseObject<User>>

    @POST("user/")
    fun addUser(@Body user: User): Call<ResponseObject<User>>

    //PARTICIPANT
    @POST("participant/{uid}&{roomId}")
    suspend fun addParticipant(
        @Path("uid") uid: String,
        @Path("roomId") roomId: String,
    ): Response<ResponseObject<Participant>>


    @GET("participant/room/{roomID}")
    fun getParticipantByRoomID(@Path("roomID") roomID: String) : Call<ResponseObject<List<Participant>>>

    @GET("participant/room/{roomID}")
    suspend fun getParticipantByRoomIDSuspend(@Path("roomID") roomID: String): Response<ResponseObject<List<Participant>>>

    @PUT("participant/{id}")
    fun updateParticipant(@Path("id") id: String, @Body body: HashMap<String, String>): Call<ResponseObject<Any>>

    //MESSAGE
    @GET("message/page/{roomID}&{page}&{step}")
    suspend fun getMessagesByRoomID(
        @Path("roomID") roomID: String,
        @Path("page") page: Int,
        @Path("step") step: Int
    ): Response<ResponseObject<List<ChatMessage>>>

    @POST("message/{uid}&{roomID}")
    fun createMessage(
        @Path("uid") uid: String,
        @Path("roomID") roomID: String,
        @Body body: HashMap<String, String>
    ): Call<ResponseObject<Message>>


}