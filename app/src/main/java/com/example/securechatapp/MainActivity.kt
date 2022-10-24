package com.example.securechatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.securechatapp.api.API
import com.example.securechatapp.model.Playlist
import com.example.securechatapp.model.ResponseObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        getAllPlaylist()
//        getPlaylistByID()
        createPlaylist()

    }

    private fun getAllPlaylist() {
        API.apiService.getAllPlaylist()
            .enqueue(object : Callback<ResponseObject<MutableList<Playlist>>> {
                override fun onResponse(
                    call: Call<ResponseObject<MutableList<Playlist>>>,
                    response: Response<ResponseObject<MutableList<Playlist>>>
                ) {

                    val playlist = response.body()?.data

                    playlist?.forEach {
                        Log.e(
                            "tuan", """
                        {
                            "id" = ${it.id},
                            "name" = ${it.name},
                            "thumbnail" = ${it.thumbnail}
                        }
                    """.trimIndent()
                        )
                    }

                }

                override fun onFailure(
                    call: Call<ResponseObject<MutableList<Playlist>>>,
                    t: Throwable
                ) {
                    Log.e("tuan", "Fetch fail: ${t.message}")
                }

            })
    }

    private fun getPlaylistByID() {
        API.apiService.getPlaylistByID(2).enqueue(object : Callback<ResponseObject<Playlist>> {
            override fun onResponse(
                call: Call<ResponseObject<Playlist>>,
                response: Response<ResponseObject<Playlist>>
            ) {

                val playlist = response.body()?.data
                Log.e("tuan", playlist.toString())

            }

            override fun onFailure(
                call: Call<ResponseObject<Playlist>>,
                t: Throwable
            ) {
                Log.e("tuan", "Fetch fail: ${t.message}")
            }

        })
    }

    private fun createPlaylist() {
        val body = HashMap<String, String>().apply {
            set("name", "new name")
            set("thumb", "new thumb")
        }

        API.apiService.createPlaylist(body).enqueue(object : Callback<ResponseObject<Unit>>{
            override fun onResponse(
                call: Call<ResponseObject<Unit>>,
                response: Response<ResponseObject<Unit>>
            ) {
                val message = response.body()?.responseMessage
            }

            override fun onFailure(call: Call<ResponseObject<Unit>>, t: Throwable) {
                Log.e("tuan", "Fetch fail: ${t.message}")
            }

        })
    }
}