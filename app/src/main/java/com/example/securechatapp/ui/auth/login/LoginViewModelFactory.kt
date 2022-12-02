package com.example.securechatapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.LocalRepository

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val localRepository: LocalRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(localRepository) as T
    }
}