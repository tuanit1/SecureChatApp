package com.example.securechatapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.model.api.NetworkResult
import com.example.securechatapp.data.model.api.ResponseError
import com.example.securechatapp.data.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ProgressViewState(val isShow: Boolean = false)

abstract class BaseViewModel : ViewModel() {

    private val _progressState = MutableStateFlow(ProgressViewState())
    val progressState: StateFlow<ProgressViewState> = _progressState.asStateFlow()
    @Inject lateinit var mLocalRepository: LocalRepository

    fun <T> handleApiResponse(
        request: suspend () -> NetworkResult<T>,
        onApiSuccess: (NetworkResult.Success<T>) -> Unit,
        onApiFailure: () -> Unit = {}
    ) {
        _progressState.update { ProgressViewState(true) }
        viewModelScope.launch(Dispatchers.IO) {
            async { request() }.run {
                await().let {
                    _progressState.update { ProgressViewState(false) }
                    when (it) {
                        is NetworkResult.Success -> {
                            withContext(Dispatchers.Main) {
                                onApiSuccess(it)
                            }
                        }
                        is NetworkResult.Error -> {
                            when(it.code) {
                                ResponseError.ACCESS_TOKEN_EXPIRED -> {
                                    mLocalRepository.refreshToken()
                                }
                                ResponseError.REFRESH_TOKEN_EXPIRED, ResponseError.REFRESH_TOKEN_INVALID -> {
                                    //kick user out
                                }
                                else -> withContext(Dispatchers.Main) {
                                    onApiFailure()
                                }
                            }
                        }
                        is NetworkResult.Exception -> {
                            withContext(Dispatchers.Main) {
                                onApiFailure()
                            }
                        }
                    }
                }
            }
        }
    }

    fun handleApiResponses(
        requests: List<suspend () -> NetworkResult<Any>>,
        onApiSuccess: (NetworkResult.Success<Any>) -> Unit,
        onApiFailure: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            requests.map { async { it() } }
                .map { it.await() }
                .let { responses ->
                    val a = 2
                    responses
                }
                .forEach {
                    when (it) {
                        is NetworkResult.Success -> {
                            onApiSuccess(it)
                        }
                        is NetworkResult.Error -> {
                            onApiFailure()
                        }
                        is NetworkResult.Exception -> {
                            onApiFailure()
                        }
                    }
                }
        }
    }
}