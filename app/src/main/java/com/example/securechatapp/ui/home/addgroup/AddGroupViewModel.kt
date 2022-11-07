package com.example.securechatapp.ui.home.addgroup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.encodeBase64
import com.example.securechatapp.utils.Constant
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddGroupViewModel(private val repository: UserRepository): ViewModel() {
    var mUsers: MutableLiveData<MutableList<User>> = MutableLiveData()
    var isLoaded = false
    var onAddGroupListener: (Boolean) -> Unit = {}

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

        viewModelScope.launch {

            val body = HashMap<String, String>().apply {
                set("name", groupName.encodeBase64())
                set("image_ic", "")
            }


            val roomResponse = API.apiService.addRoom(Constant.mUID, body)

            if(roomResponse.success){
                roomResponse.data?.run {

                    try {
                        val selectedList = mUsers.value?.filter { user -> user.isSelected == true }
                        selectedList?.forEach { user ->
                            API.apiService.addParticipant(user.uid, this.id)
                        }
                        Log.e("tuan", "Done all participant")
                        onAddGroupListener(true)


                    }catch (e: Exception){
                        Log.e("tuan", "add participant fail")
                        onAddGroupListener(false)
                    }

                }
            }else{
                Log.e("tuan", "add room failed")
                onAddGroupListener(false)
            }
        }

    }

    fun checkUser(index: Int, isCheck: Boolean){
        mUsers.value?.get(index)?.isSelected = isCheck
    }
}