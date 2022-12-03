package com.example.securechatapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.api.AuthToken
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val localRepository: LocalRepository
) :ViewModel() {
    fun getAuthToken(callback: APICallback){
        callback.onStart()
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getAuthToken().enqueue(object : Callback<ResponseObject<AuthToken>>{
                override fun onResponse(
                    call: Call<ResponseObject<AuthToken>>,
                    response: Response<ResponseObject<AuthToken>>
                ) {
                    if(response.isSuccessful && response.body()?.success == true){
                        response.body()?.data?.let { authToken ->
                            localRepository.saveAccessToken(authToken.accessToken)
                            localRepository.saveRefreshToken(authToken.refreshToken)
                            callback.onSuccess()
                        } ?: callback.onError()

                    }else{
                        callback.onError()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseObject<AuthToken>>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }

            })
        }
    }
}