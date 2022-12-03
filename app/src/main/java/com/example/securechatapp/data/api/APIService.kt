package com.example.securechatapp.data.api

import com.example.securechatapp.data.model.*
import com.example.securechatapp.data.model.api.AuthToken
import com.example.securechatapp.data.model.api.ResponseObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    //AUTH
    @POST("auth/login/{uid}")
    fun getAuthToken(@Path("uid") uid: String): Call<ResponseObject<AuthToken>>

    @POST("auth/token")
    fun refreshToken(@Body body: HashMap<String, String>): Call<ResponseObject<AuthToken>>

    //ROOM
    @GET("room/{uid}")
    fun getRoomList(@Path("uid") uid: String): Call<ResponseObject<MutableList<ChatRoom>>>

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
    fun getRoomByID(@Path("uid") uid: String, @Path("roomID") roomID: String): Call<ResponseObject<ChatRoom>>

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

    //MESSAGE
    @GET("message/page/{roomID}&{page}&{step}")
    fun getMessagesByRoomID(
        @Path("roomID") roomID: String,
        @Path("page") page: Int,
        @Path("step") step: Int
    ): Call<ResponseObject<List<ChatMessage>>>

    @POST("message/{uid}&{roomID}")
    fun createMessage(
        @Path("uid") uid: String,
        @Path("roomID") roomID: String,
        @Body body: HashMap<String, String>
    ): Call<ResponseObject<Message>>

}