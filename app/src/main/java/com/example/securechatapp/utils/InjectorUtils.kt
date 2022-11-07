package com.example.securechatapp.utils

import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.ui.home.addgroup.AddGroupViewModel
import com.example.securechatapp.ui.home.addgroup.AddGroupViewModelFactory
import com.example.securechatapp.ui.home.chatlist.ChatListViewModelFactory
import com.example.securechatapp.ui.home.userlist.UserListViewModelFactory

object InjectorUtils {
    fun provideChatListViewModelFactory(): ChatListViewModelFactory {
        val repository = ChatListRepository()
        return ChatListViewModelFactory(repository)
    }

    fun provideAddGroupViewModelFactory(): AddGroupViewModelFactory {
        val repository = UserRepository()
        return AddGroupViewModelFactory(repository)
    }

    fun provideUserListViewModelFactory(): UserListViewModelFactory {
        val userRepository = UserRepository()
        val roomRepository = RoomRepository()
        return UserListViewModelFactory(userRepository, roomRepository)
    }
}