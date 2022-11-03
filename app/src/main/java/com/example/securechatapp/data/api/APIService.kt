package com.example.securechatapp.data.api

import com.example.securechatapp.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET("playlist/getAll.php")
    fun getAllPlaylist(): Call<ResponseObject<MutableList<Playlist>>>

    @GET("playlist/getByID.php")
    fun getPlaylistByID(@Query("id") ID: Int): Call<ResponseObject<Playlist>>

    @HTTP(method = "POST", path = "playlist/create.php", hasBody = true)
    fun createPlaylist(@Body body: Map<String, String>): Call<ResponseObject<Unit>>

    //ROOM
    @GET("room/group/{uid}")
    fun getRoomList(@Path("uid") uid: String): Call<ResponseObject<MutableList<ChatRoom>>>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
    @POST("participant/{uid}&{roomId}")
    fun addRoom(
        @Path("uid") uid: String,
        @Path("roomId") roomId: String,
        @Body participant: HashMap<String, out Any>
    ): Call<ResponseObject<User>>


    //USER
    @GET("user")
    fun getAllUser(): Call<ResponseObject<MutableList<User>>>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
    @POST("user/")
    fun addUser(@Body user: User): Call<ResponseObject<User>>

    //PARTICIPANT
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk"
    )
    @POST("participant/{uid}&{roomId}")
    fun addParticipant(
        @Path("uid") uid: String,
        @Path("roomId") roomId: String,
        @Body participant: HashMap<String, out Any>
    ): Call<ResponseObject<User>>


}