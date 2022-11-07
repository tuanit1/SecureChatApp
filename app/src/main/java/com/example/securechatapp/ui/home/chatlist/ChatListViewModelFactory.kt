package com.example.securechatapp.ui.home.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class ChatListViewModelFactory(
    private val chatRepository: ChatListRepository,
    private val userRepository: UserRepository)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatListViewModel(chatRepository, userRepository) as T
    }
}