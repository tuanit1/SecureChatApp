package com.example.securechatapp.data.api

import android.content.Context
import android.util.Log
import com.example.securechatapp.data.model.api.ResponseError
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.utils.Constant
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object API {
    private var retrofit: Retrofit? = null
    private var localRepository: LocalRepository? = null
    lateinit var apiService: APIService

    fun initAPIService(context: Context){
        localRepository = LocalRepository(context)

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://${Constant.SERVER_URL}:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
        apiService = retrofit!!.create(APIService::class.java)
    }

    private val httpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().apply {
                    addHeader("Authorization", "Bearer ${localRepository?.getAccessToken()}")
                }
                chain.proceed(builder.build())
            }
            .build()

    suspend fun <T>checkTokenExpired(
        response: Response<ResponseObject<T>>,
        onTokenInUse: () -> Unit,
        onTokenUpdated: () -> Unit,
        onRefreshTokenExpired: () -> Unit,
        onError: () -> Unit
    ){

        if(!response.isSuccessful){
            val responseError: ResponseError = Gson().fromJson(response.errorBody()?.string(), ResponseError::class.java)

            when(responseError.code){
                ResponseError.ACCESS_TOKEN_EXPIRED -> {

                    Log.e("tuan", "access token expired")

                    val body = HashMap<String, String>().apply {
                        set("refreshToken", localRepository?.getRefreshToken().toString())
                    }

                    val refreshResponse = apiService.refreshToken(body)

                    if(!refreshResponse.isSuccessful){
                        val refreshError: ResponseError = Gson().fromJson(refreshResponse.errorBody()?.string(), ResponseError::class.java)
                        when(refreshError.code){
                            ResponseError.REFRESH_TOKEN_EXPIRED, ResponseError.REFRESH_TOKEN_INVALID -> {
                                Log.e("tuan", "refresh token expired or invalid")
                                withContext(Dispatchers.Main){
                                    onRefreshTokenExpired()
                                }
                            }
                            else -> run {
                                withContext(Dispatchers.Main){
                                    onError()
                                }
                            }
                        }
                    }else{
                        Log.e("tuan", "access token updated")
                        localRepository?.run {
                            if(refreshResponse.body()?.success == true){
                                refreshResponse.body()?.data?.let { authToken ->
                                    saveAccessToken(authToken.accessToken)
                                    saveRefreshToken(authToken.refreshToken)
                                    onTokenUpdated()
                                } ?: run {
                                    withContext(Dispatchers.Main){
                                        onError()
                                    }
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    onError()
                                }
                            }
                        }
                    }
                }
                else -> onError()
            }
        }else{
            withContext(Dispatchers.Main){
                onTokenInUse()
            }
        }
    }

    suspend fun <D, U, T> checkTokenExpiredThreeRequest(
        response1: Response<ResponseObject<D>>,
        response2: Response<ResponseObject<U>>,
        response3: Response<ResponseObject<T>>,
        onTokenInUse: () -> Unit,
        onTokenUpdated: () -> Unit,
        onRefreshTokenExpired: () -> Unit,
        onError: () -> Unit
    ){

        if(!response1.isSuccessful && !response2.isSuccessful){
            val resError1: ResponseError = Gson().fromJson(response1.errorBody()?.string(), ResponseError::class.java)
            val resError2: ResponseError = Gson().fromJson(response2.errorBody()?.string(), ResponseError::class.java)
            val resError3: ResponseError = Gson().fromJson(response3.errorBody()?.string(), ResponseError::class.java)

            when(ResponseError.ACCESS_TOKEN_EXPIRED){
                resError1.code, resError2.code, resError3.code -> {

                    Log.e("tuan", "access token expired")

                    val body = HashMap<String, String>().apply {
                        set("refreshToken", localRepository?.getRefreshToken().toString())
                    }

                    val refreshResponse = apiService.refreshToken(body)

                    if(!refreshResponse.isSuccessful){
                        val refreshError: ResponseError = Gson().fromJson(refreshResponse.errorBody()?.string(), ResponseError::class.java)
                        when(refreshError.code){
                            ResponseError.REFRESH_TOKEN_EXPIRED, ResponseError.REFRESH_TOKEN_INVALID -> {
                                Log.e("tuan", "refresh token expired or invalid")
                                withContext(Dispatchers.Main){
                                    onRefreshTokenExpired()
                                }
                            }
                            else -> run {
                                withContext(Dispatchers.Main){
                                    onError()
                                }
                            }
                        }
                    }else{
                        Log.e("tuan", "access token updated")
                        localRepository?.run {
                            if(refreshResponse.body()?.success == true){
                                refreshResponse.body()?.data?.let { authToken ->
                                    saveAccessToken(authToken.accessToken)
                                    saveRefreshToken(authToken.refreshToken)
                                    onTokenUpdated()
                                } ?: run {
                                    withContext(Dispatchers.Main){
                                        onError()
                                    }
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    onError()
                                }
                            }
                        }
                    }
                }
                else -> {
                    withContext(Dispatchers.Main){
                        onError()
                    }
                }
            }
        }else{
            withContext(Dispatchers.Main){
                onTokenInUse()
            }
        }
    }
}