package com.example.securechatapp.data.datasources

import com.example.securechatapp.data.api.API
import javax.inject.Inject

class AuthDataSource @Inject constructor() : RetrofitDataSource() {
    suspend fun refreshToken(body: HashMap<String, String>) = safeCallApi { API.apiService.refreshToken(body) }
}