package com.example.securechatapp.ui.home.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.ChatListRepository

@Suppress("UNCHECKED_CAST")
class ChatListViewModelFactory(private val repository: ChatListRepository)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatListViewModel(repository) as T
    }
}