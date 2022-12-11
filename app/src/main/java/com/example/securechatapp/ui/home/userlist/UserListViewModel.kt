package com.example.securechatapp.ui.home.userlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListViewModel(
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {
    var mUsers: MutableLiveData<MutableList<User>> = MutableLiveData()
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadUserList(callback: APICallback) {

        callback.onStart()

        userRepository.getAllUser()
            .enqueue(object : Callback<ResponseObject<MutableList<User>>> {
                override fun onResponse(
                    call: Call<ResponseObject<MutableList<User>>>,
                    response: Response<ResponseObject<MutableList<User>>>
                ) {
                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                response.body()?.let { body ->
                                    if (body.success) {
                                        body.data?.let { data ->
                                            val filteredList = data.filter { user -> user.uid != Constant.mUID }
                                            mUsers.value = filteredList.toMutableList()
                                        }

                                        callback.onSuccess()
                                    } else {
                                        Log.e("tuan", "status: false")
                                        callback.onError()
                                    }
                                }
                            },
                            onTokenUpdated = {
                                loadUserList(callback)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = { callback.onError() }
                        )
                    }

                }

                override fun onFailure(
                    call: Call<ResponseObject<MutableList<User>>>,
                    t: Throwable
                ) {
                    Log.e("tuan", t.message.toString())
                    callback.onError(t)
                }

            })
    }

    fun openPrivateChatScreen(
        otherUID: String,
        callback: (Boolean, String?) -> Unit,
    ) {

        viewModelScope.launch {
            try {
                val roomResponse = roomRepository.getPrivateRoom(
                    currentUID = Constant.mUID,
                    otherUID = otherUID
                )

                if (roomResponse.success) {
                    roomResponse.data?.let { chatRoom ->
                        if(chatRoom.room == null){
                            val newRoomResponse = API.apiService.createPrivateRoom(
                                currentUID = Constant.mUID,
                                otherUID = otherUID
                            )

                            if(newRoomResponse.success){
                                callback(true, newRoomResponse.data?.id)
                            }else{
                                Log.e("tuan", "${this.javaClass.name}: createNewRoom() fail")
                                callback(false, null)
                            }

                        }else{
                            Log.e("tuan", "room exist")
                            callback(true, chatRoom.room?.id)
                        }
                    }
                } else {
                    Log.e("tuan", "${this.javaClass.name}: getPrivateRoom() fail")
                    callback(false, null)
                }
            } catch (e: Exception) {
                Log.e("tuan", "${this.javaClass.name}: ${e.message}")
                callback(false, null)
            }
        }

    }

    fun getCurrentUser(userID: String, callback: APICallback){
        viewModelScope.launch {
            userRepository.getUserByID(userID).enqueue(object : Callback<ResponseObject<User>> {
                override fun onResponse(
                    call: Call<ResponseObject<User>>,
                    response: Response<ResponseObject<User>>
                ) {

                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if(response.isSuccessful){
                                    response.body()?.data?.let { user ->
                                        callback.onSuccess(user)
                                    }
                                }else{
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
    }
}