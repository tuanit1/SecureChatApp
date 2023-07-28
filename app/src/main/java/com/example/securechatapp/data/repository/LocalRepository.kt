package com.example.securechatapp.data.repository

import android.content.Context
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.datasources.AuthDataSource
import com.example.securechatapp.data.datasources.RoomDataSource
import com.example.securechatapp.utils.Constant
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val authDataSource: AuthDataSource
) {

    companion object {
        private const val KEY_SHARED_PREFERENCE = "shared_preference_key"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOGGLE_PIN = "toggle_pin"
        private const val KEY_INT_PIN = "init_pin"
        private const val KEY_PIN = "pin"
    }

    private var sharedPref =
        context.getSharedPreferences(KEY_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    fun getAuthToken() = API.apiService.getAuthToken(Constant.mUID)

    fun saveAccessToken(token: String) {
        with(sharedPref.edit()) {
            putString(KEY_ACCESS_TOKEN, token)
            apply()
        }
    }

    suspend fun refreshToken(){
        val body = hashMapOf(
            "refreshToken" to getRefreshToken().toString()
        )
        authDataSource.refreshToken(body)
    }

    fun saveRefreshToken(token: String) {
        with(sharedPref.edit()) {
            putString(KEY_REFRESH_TOKEN, token)
            apply()
        }
    }

    fun setTogglePIN(state: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(KEY_TOGGLE_PIN, state)
            apply()
        }
    }

    fun initPIN() {
        with(sharedPref.edit()) {
            putBoolean(KEY_INT_PIN, true)
            apply()
        }
    }

    fun setPIN(pin: String) {
        with(sharedPref.edit()) {
            putString(KEY_PIN, pin)
            apply()
        }
    }

    fun checkPIN(pin: String): Boolean {
        val savePin = sharedPref?.getString(KEY_PIN, "")
        return pin == savePin
    }

    fun getAccessToken(): String? = sharedPref?.getString(KEY_ACCESS_TOKEN, "")
    fun getRefreshToken(): String? = sharedPref?.getString(KEY_REFRESH_TOKEN, "")
    fun isTogglePIN(): Boolean? = sharedPref?.getBoolean(KEY_TOGGLE_PIN, false)
    fun isInitPIN(): Boolean? = sharedPref?.getBoolean(KEY_INT_PIN, false)

}