package com.example.securechatapp.data.api

import com.example.securechatapp.data.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    //AUTH
    @POST("auth/login/{uid}")
    fun getAuthToken(@Path("uid") uid: String): Call<ResponseObject<AuthToken>>

    //ROOM
    @GET("room/{uid}")
    fun getRoomList(@Path("uid") uid: String): Call<ResponseObject<MutableList<ChatRoom>>>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
    @POST("room/{uid}")
    suspend fun addRoom(
        @Path("uid") uid: String,
        @Body body: HashMap<String, String>
    ): ResponseObject<Room>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
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
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
    @POST("participant/{uid}&{roomId}")
    suspend fun addParticipant(
        @Path("uid") uid: String,
        @Path("roomId") roomId: String,
    ): ResponseObject<Participant>

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