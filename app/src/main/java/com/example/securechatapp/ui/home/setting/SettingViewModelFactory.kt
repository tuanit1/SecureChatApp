package com.example.securechatapp.ui.home.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class SettingViewModelFactory(
    private val userRepository: UserRepository,
    private val localRepository: LocalRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(userRepository, localRepository) as T
    }
}