package com.example.securechatapp.data.api

import com.example.securechatapp.data.model.Playlist
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.User
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET("playlist/getAll.php")
    fun getAllPlaylist(): Call<ResponseObject<MutableList<Playlist>>>

    @GET("playlist/getByID.php")
    fun getPlaylistByID(@Query("id") ID: Int): Call<ResponseObject<Playlist>>

    @HTTP(method = "POST", path = "playlist/create.php", hasBody = true)
    fun createPlaylist(@Body body: Map<String, String>): Call<ResponseObject<Unit>>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoiNjJkN2I4YWYyNmE1NTliZDhmOTRkNDA3IiwiaWF0IjoxNjU4Nzk5MDIyfQ.Xi-0E_F_5aqI_zICxPre-4XgRUIazLVIk3iJUviN1gk")
    @POST("user/")
    fun addUser(@Body user: User): Call<ResponseObject<User>>


}