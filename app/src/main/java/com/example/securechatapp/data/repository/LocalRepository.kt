package com.example.securechatapp.data.repository

import com.example.securechatapp.data.api.API
import com.example.securechatapp.utils.Constant

class LocalRepository {
    fun getAuthToken() = API.apiService.getAuthToken(Constant.mUID)
}