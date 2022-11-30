package com.example.securechatapp.ui.home.chatlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.utils.AppSocket
import com.example.securechatapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class ChatListViewModel(
    private val chatRepository: ChatListRepository,
    private val userRepository: UserRepository
    ): ViewModel() {

    var mChatRooms: MutableLiveData<MutableList<ChatRoom>> = MutableLiveData()

    fun loadRoomList(uid: String, callback: APICallback? = null){
        callback?.onStart()
        chatRepository.getRoomList(uid).enqueue(object : Callback<ResponseObject<MutableList<ChatRoom>>>{
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

    fun updateLatestMessage(m: Message) {

        mChatRooms.value?.forEach {  chatRoom ->
            if(chatRoom.room?.id == m.roomID){
                chatRoom.message = m
            }
        }

        mChatRooms.postValue(mChatRooms.value)
    }

    fun getCurrentUser(userID: String, callback: APICallback){
        viewModelScope.launch {
            userRepository.getUserByID(userID).enqueue(object : Callback<ResponseObject<User>>{
                override fun onResponse(
                    call: Call<ResponseObject<User>>,
                    response: Response<ResponseObject<User>>
                ) {
                    if(response.isSuccessful){
                        response.body()?.data?.let { user ->
                            callback.onSuccess(user)
                        }
                    }else{
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call<ResponseObject<User>>, t: Throwable) {
                    callback.onError(t)
                }
            })
        }
    }

    fun addNewRoomToList(roomID: String){
        try {
            viewModelScope.launch {
                API.apiService.getRoomByID(Constant.mUID, roomID).enqueue(object : Callback<ResponseObject<ChatRoom>>{
                    override fun onResponse(
                        call: Call<ResponseObject<ChatRoom>>,
                        response: Response<ResponseObject<ChatRoom>>
                    ) {
                        if(response.isSuccessful){
                            response.body()?.data?.let{ chatRoom ->
                                mChatRooms.value?.toMutableList()?.apply {
                                    add(chatRoom)
                                    mChatRooms.postValue(this)
                                }
                            }
                        }else{
                            Log.e("tuan", "addNewRoomToList failed")
                        }
                    }

                    override fun onFailure(call: Call<ResponseObject<ChatRoom>>, t: Throwable) {
                        Log.e("tuan", "addNewRoomToList failed ${t.message}")
                    }

                })

            }

        }catch (e: Exception){
            Log.e("tuan", "addNewRoomToList failed")
        }
    }
}