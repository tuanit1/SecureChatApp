package com.example.securechatapp.ui.home.chatscreen.chatsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.ParticipantRepository

@Suppress("UNCHECKED_CAST")
class ChatSettingViewModelFactory(
    private val participantRepository: ParticipantRepository
)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatSettingViewModel(participantRepository) as T
    }
}