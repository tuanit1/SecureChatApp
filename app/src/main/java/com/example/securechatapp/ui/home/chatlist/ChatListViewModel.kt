package com.example.securechatapp.ui.home.chatlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.model.api.NetworkResult
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatListRepository,
    private val userRepository: UserRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    var mChatRooms: MutableLiveData<MutableList<ChatRoom>> = MutableLiveData()
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadRoomList(uid: String, callback: APICallback? = null) {
        callback?.onStart()
        viewModelScope.launch {
            val results = listOf(
                chatRepository.getRoomList(uid),
                chatRepository.getRoomByID(uid, "6364e01fa7ec08498dd9ba9e"),
            )
                .map { async { it } }
                .map { it.await() }
                .asFlow()
                .collect {
                    when(it) {
                        is NetworkResult.Success -> {
                            when (it.data) {
                                is ResponseObject.GetRoomListData -> {
                                    val a = it.data.data
                                }
                                is ResponseObject.GetRoomByIdData -> {
                                    val b = 2
                                }
                            }
                        }
                        else -> {
                            val b = 3
                        }
                    }
                }
        }

//        chatRepository.getRoomListA(uid)
//            .enqueue(object : Callback<ResponseObject<MutableList<ChatRoom>>> {
//                override fun onResponse(
//                    call: Call<ResponseObject<MutableList<ChatRoom>>>,
//                    response: Response<ResponseObject<MutableList<ChatRoom>>>
//                ) {
//                    viewModelScope.launch(Dispatchers.IO) {
//                        API.checkTokenExpired(
//                            response,
//                            onTokenInUse = {
//                                response.body()?.let { body ->
//                                    if (body.success) {
//                                        body.data?.let { data ->
//                                            mChatRooms.value = data
//                                        }
//
//                                        callback?.onSuccess()
//                                    } else {
//                                        Log.e("tuan", "status: false")
//                                        callback?.onError()
//                                    }
//                                }
//                            },
//                            onTokenUpdated = {
//                                loadRoomList(uid, callback)
//                            },
//                            onRefreshTokenExpired = {
//                                isTokenExpired.value = true
//                            },
//                            onError = {
//                                callback?.onError()
//                            }
//                        )
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<ResponseObject<MutableList<ChatRoom>>>,
//                    t: Throwable
//                ) {
//                    Log.e("tuan", t.message.toString())
//                    callback?.onError(t)
//                }
//
//            })
    }

    fun updateLatestMessage(m: Message) {

        mChatRooms.value?.forEach { chatRoom ->
            if (chatRoom.room?.id == m.roomID) {
                chatRoom.message = m
            }
        }

        mChatRooms.postValue(mChatRooms.value)
    }

    fun getCurrentUser(userID: String, callback: APICallback) {
        userRepository.getUserByID(userID).enqueue(object : Callback<ResponseObject<User>> {
            override fun onResponse(
                call: Call<ResponseObject<User>>,
                response: Response<ResponseObject<User>>
            ) {
                viewModelScope.launch(Dispatchers.IO) {
                    API.checkTokenExpired(
                        response,
                        onTokenInUse = {
                            if (response.isSuccessful) {
                                response.body()?.data?.let { user ->
                                    callback.onSuccess(user)
                                }
                            } else {
                                callback.onError()
                            }
                        },
                        onTokenUpdated = {
                            getCurrentUser(userID, callback)
                        },
                        onRefreshTokenExpired = {
                            isTokenExpired.value = true
                        },
                        onError = { callback.onError() }
                    )
                }
            }

            override fun onFailure(call: Call<ResponseObject<User>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun addNewRoomToList(roomID: String) {
//        try {
//            viewModelScope.launch {
//                val response = API.apiService.getRoomByID(Constant.mUID, roomID)
//
//                API.checkTokenExpired(
//                    response,
//                    onTokenInUse = {
//                        launch(Dispatchers.Main) {
//                            if (response.isSuccessful) {
//                                response.body()?.data?.let { chatRoom ->
//                                    mChatRooms.value?.toMutableList()?.apply {
//                                        add(chatRoom)
//                                        mChatRooms.postValue(this)
//                                    }
//                                }
//                            } else {
//                                Log.e("tuan", "addNewRoomToList failed")
//                            }
//                        }
//                    },
//                    onTokenUpdated = {
//                        addNewRoomToList(roomID)
//                    },
//                    onRefreshTokenExpired = {
//                        isTokenExpired.value = true
//                    },
//                    onError = {}
//                )
//
//            }
//
//        } catch (e: Exception) {
//            Log.e("tuan", "addNewRoomToList failed")
//        }
    }

    fun checkPIN(pin: String) = localRepository.checkPIN(pin)

    fun checkIsTogglePIN() = localRepository.isTogglePIN()
}