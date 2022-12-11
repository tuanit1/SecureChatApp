package com.example.securechatapp.ui.home.setting.setuppin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.ui.home.setting.SettingViewModel

@Suppress("UNCHECKED_CAST")
class SetupPinViewModelFactory(
    private val localRepository: LocalRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SetupPinViewModel(localRepository) as T
    }
}