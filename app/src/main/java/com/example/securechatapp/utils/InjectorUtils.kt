package com.example.securechatapp.utils

import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.ui.home.chatlist.ChatListViewModelFactory

object InjectorUtils {
    fun provideChatListViewModelFactory(): ChatListViewModelFactory {
        val repository = ChatListRepository()
        return ChatListViewModelFactory(repository)
    }
}