package com.example.securechatapp.ui.home.userlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListViewModel(private val repository: UserRepository): ViewModel() {
    var mUsers: MutableLiveData<MutableList<User>> = MutableLiveData()

    fun loadUserList(callback: APICallback){

        callback.onStart()

        repository.getAllUser().enqueue(object : retrofit2.Callback<ResponseObject<MutableList<User>>>{
            override fun onResponse(
                call: Call<ResponseObject<MutableList<User>>>,
                response: Response<ResponseObject<MutableList<User>>>
            ) {
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
            }

            override fun onFailure(call: Call<ResponseObject<MutableList<User>>>, t: Throwable) {
                Log.e("tuan", t.message.toString())
                callback.onError(t)
            }

        })
    }
}