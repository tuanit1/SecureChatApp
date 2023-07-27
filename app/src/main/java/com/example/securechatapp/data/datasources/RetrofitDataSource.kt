package com.example.securechatapp.data.datasources

import com.example.securechatapp.data.model.api.NetworkResult
import com.example.securechatapp.data.model.api.ResponseObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

open class RetrofitDataSource {
    suspend fun <T : Any> safeCallApi(
        execute: suspend () -> Response<out ResponseObject<T>>
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NetworkResult.Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }
}