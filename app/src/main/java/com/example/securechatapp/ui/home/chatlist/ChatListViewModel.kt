package com.example.securechatapp.ui.home.chatlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.data.repository.ChatListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListViewModel(private val repository: ChatListRepository): ViewModel() {

    var mChatRooms: MutableLiveData<MutableList<ChatRoom>> = MutableLiveData()

    fun loadRoomList(uid: String, callback: APICallback? = null){
        callback?.onStart()
        repository.getRoomList(uid).enqueue(object : Callback<ResponseObject<MutableList<ChatRoom>>>{
            override fun onResponse(
                call: Call<ResponseObject<MutableList<ChatRoom>>>,
                response: Response<ResponseObject<MutableList<ChatRoom>>>
            ) {

                response.body()?.let { body ->
                    if (body.success) {
                        body.data?.let { data ->
                            mChatRooms.value = data
                        }

                        callback?.onSuccess()
                    } else {
                        Log.e("tuan", "status: false")
                        callback?.onError()
                    }
                }
            }

            override fun onFailure(
                call: Call<ResponseObject<MutableList<ChatRoom>>>,
                t: Throwable
            ) {
                Log.e("tuan", t.message.toString())
                callback?.onError(t)
            }

        })
    }
}