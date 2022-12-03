package com.example.securechatapp.ui.home.addgroup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.extension.encodeBase64
import com.example.securechatapp.utils.Constant
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddGroupViewModel(private val repository: UserRepository) : ViewModel() {
    var mUsers: MutableLiveData<MutableList<User>> = MutableLiveData()
    var isLoaded = false
    var onAddGroupListener: (Boolean, String?) -> Unit = { _, _ -> }
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadUserList(callback: APICallback) {

        callback.onStart()

        repository.getAllUser()
            .enqueue(object : Callback<ResponseObject<MutableList<User>>> {
                override fun onResponse(
                    call: Call<ResponseObject<MutableList<User>>>,
                    response: Response<ResponseObject<MutableList<User>>>
                ) {

                    API.checkTokenExpired(
                        response,
                        onTokenInUse = {
                            response.body()?.let { body ->
                                if (body.success) {
                                    body.data?.let { data ->
                                        val filteredList =
                                            data.filter { user -> user.uid != Constant.mUID }
                                        mUsers.value = filteredList.toMutableList()
                                    }

                                    isLoaded = true

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

                override fun onFailure(
                    call: Call<ResponseObject<MutableList<User>>>,
                    t: Throwable
                ) {
                    Log.e("tuan", t.message.toString())
                    callback.onError(t)
                }

            })
    }

    fun addNewGroup(groupName: String) {

        viewModelScope.launch {

            val body = HashMap<String, String>().apply {
                set("name", groupName.encodeBase64())
                set("image_ic", "")
            }

            API.apiService.addRoom(Constant.mUID, body)
                .enqueue(object : Callback<ResponseObject<Room>> {
                    override fun onResponse(
                        call: Call<ResponseObject<Room>>,
                        response: Response<ResponseObject<Room>>
                    ) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if (response.body()?.success == true) {
                                    response.body()?.data?.let { room ->
                                        try {
                                            val selectedList =
                                                mUsers.value?.filter { user -> user.isSelected == true }
                                            selectedList?.forEach { user ->
                                                room.id?.let {
                                                    viewModelScope.launch {
                                                        val res = API.apiService.addParticipant(
                                                            user.uid,
                                                            it
                                                        )

                                                        API.checkTokenExpired(
                                                            res,
                                                            onTokenInUse = {},
                                                            onTokenUpdated = {
                                                                viewModelScope.launch {
                                                                    API.apiService.addParticipant(
                                                                        user.uid,
                                                                        it
                                                                    )
                                                                }
                                                            },
                                                            onRefreshTokenExpired = {
                                                                isTokenExpired.value = true
                                                            },
                                                            onError = {}
                                                        )
                                                    }
                                                }
                                            }
                                            Log.e("tuan", "Done all participant")
                                            onAddGroupListener(true, room.id)


                                        } catch (e: Exception) {
                                            Log.e("tuan", "add participant fail")
                                            onAddGroupListener(false, null)
                                        }

                                    }
                                } else {
                                    Log.e("tuan", "add room failed")
                                    onAddGroupListener(false, null)
                                }
                            },
                            onTokenUpdated = {
                                addNewGroup(groupName)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = {}
                        )
                    }

                    override fun onFailure(call: Call<ResponseObject<Room>>, t: Throwable) {
                        Log.e(
                            "tuan",
                            this@AddGroupViewModel.javaClass.name + ": " + t.message.toString()
                        )
                    }

                })


        }

    }

    fun checkUser(index: Int, isCheck: Boolean) {
        mUsers.value?.get(index)?.isSelected = isCheck
    }
}