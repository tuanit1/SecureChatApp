package com.example.securechatapp.data.repository

import android.content.Context
import com.example.securechatapp.data.api.API
import com.example.securechatapp.utils.Constant

class LocalRepository(context: Context) {

    companion object{
        private const val KEY_SHARED_PREFERENCE = "shared_preference_key"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    private var sharedPref = context.getSharedPreferences(KEY_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    fun getAuthToken() = API.apiService.getAuthToken(Constant.mUID)

    fun saveAccessToken(token: String){
        with(sharedPref.edit()){
            putString(KEY_ACCESS_TOKEN, token)
            apply()
        }
    }

    fun saveRefreshToken(token: String){
        with(sharedPref.edit()){
            putString(KEY_REFRESH_TOKEN, token)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPref?.getString(KEY_ACCESS_TOKEN, "")
    fun getRefreshToken(): String? = sharedPref?.getString(KEY_REFRESH_TOKEN, "")


}