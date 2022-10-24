package com.example.securechatapp.api

import com.example.securechatapp.model.Playlist
import com.example.securechatapp.model.ResponseObject
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET("playlist/getAll.php")
    fun getAllPlaylist(): Call<ResponseObject<MutableList<Playlist>>>

    @GET("playlist/getByID.php")
    fun getPlaylistByID(@Query("id") ID: Int): Call<ResponseObject<Playlist>>

    @HTTP(method = "POST", path = "playlist/create.php", hasBody = true)
    fun createPlaylist(@Body body: Map<String, String>): Call<ResponseObject<Unit>>


}