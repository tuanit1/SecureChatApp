package com.example.securechatapp.ui.home.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingViewModel(
    private val userRepository: UserRepository,
    private val localRepository: LocalRepository
) : ViewModel() {
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)
    var isTogglePIN: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isTogglePIN.value = localRepository.isTogglePIN()
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

    fun updateToggleState(){
        isTogglePIN.value = localRepository.isTogglePIN()
    }

    fun isInitPin() = localRepository.isInitPIN()

    fun setTogglePINState(isCheck: Boolean){
        localRepository.setTogglePIN(isCheck)
        updateToggleState()
    }

    fun checkPin(pin: String) = localRepository.checkPIN(pin)
}