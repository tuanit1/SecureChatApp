package com.example.securechatapp.data.api

import android.content.Context
import android.util.Log
import com.example.securechatapp.data.model.api.AuthToken
import com.example.securechatapp.data.model.api.ResponseError
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.utils.Constant
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
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

    fun <T>checkTokenExpired(
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

                    apiService.refreshToken(body).enqueue(object : Callback<ResponseObject<AuthToken>>{
                        override fun onResponse(
                            call: Call<ResponseObject<AuthToken>>,
                            response: Response<ResponseObject<AuthToken>>
                        ) {
                            if(!response.isSuccessful){
                                val refreshError: ResponseError = Gson().fromJson(response.errorBody()?.string(), ResponseError::class.java)
                                when(refreshError.code){
                                    ResponseError.REFRESH_TOKEN_EXPIRED, ResponseError.REFRESH_TOKEN_INVALID -> {
                                        Log.e("tuan", "refresh token expired or invalid")
                                        onRefreshTokenExpired()
                                    }
                                    else -> onError()
                                }
                            }else{
                                Log.e("tuan", "access token updated")
                                localRepository?.run {
                                    if(response.body()?.success == true){
                                        response.body()?.data?.let { authToken ->
                                            saveAccessToken(authToken.accessToken)
                                            saveRefreshToken(authToken.refreshToken)
                                            onTokenUpdated()
                                        } ?: onError()
                                    }else{
                                        onError()
                                    }
                                }
                            }

                        }

                        override fun onFailure(
                            call: Call<ResponseObject<AuthToken>>,
                            t: Throwable
                        ) {
                            onError()
                        }

                    })
                }
                else -> onError()
            }
        }else{
            onTokenInUse()
        }
    }
}