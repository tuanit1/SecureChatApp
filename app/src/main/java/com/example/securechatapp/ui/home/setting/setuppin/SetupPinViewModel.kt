package com.example.securechatapp.ui.home.setting.setuppin

import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.repository.LocalRepository

class SetupPinViewModel(
    private val localRepository: LocalRepository
) : ViewModel() {
    fun savePin(pin: String){
        localRepository.initPIN()
        localRepository.setPIN(pin)
    }
}