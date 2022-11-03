package com.example.securechatapp.ui.home.addgroup

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.User
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class AddGroupViewModel: ViewModel() {
    var mUsers: MutableLiveData<MutableList<User>> = MutableLiveData()
    var isLoaded = false

    fun loadUserList(callback: APICallback){

        callback.onStart()

        API.apiService.getAllUser().enqueue(object : retrofit2.Callback<ResponseObject<MutableList<User>>>{
            override fun onResponse(
                call: Call<ResponseObject<MutableList<User>>>,
                response: Response<ResponseObject<MutableList<User>>>
            ) {
                response.body()?.let { body ->
                    if (body.success) {
                        body.data?.let { data ->
                            mUsers.value = data
                        }

                        isLoaded = true
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

    fun addNewGroup(groupName: String){
        val selectedList = mUsers.value?.filter { user -> user.isSelected == true }

        selectedList?.forEach { user ->

        }
    }

    fun checkUser(index: Int, isCheck: Boolean){
        mUsers.value?.get(index)?.isSelected = isCheck
    }
}