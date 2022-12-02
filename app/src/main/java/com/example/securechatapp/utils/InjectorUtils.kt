package com.example.securechatapp.utils

import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.MessageRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.ui.auth.login.LoginViewModelFactory
import com.example.securechatapp.ui.home.addgroup.AddGroupViewModelFactory
import com.example.securechatapp.ui.home.chatlist.ChatListViewModelFactory
import com.example.securechatapp.ui.home.chatscreen.ChatScreenViewModelFactory
import com.example.securechatapp.ui.home.userlist.UserListViewModelFactory

object InjectorUtils {
    fun provideChatListViewModelFactory(): ChatListViewModelFactory {
        val chatRepository = ChatListRepository()
        val userRepository = UserRepository()
        return ChatListViewModelFactory(chatRepository, userRepository)
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

    fun provideChatScreenViewModelFactory(): ChatScreenViewModelFactory {
        val messageRepository = MessageRepository()
        val roomRepository = RoomRepository()
        return ChatScreenViewModelFactory(roomRepository, messageRepository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        val localRepository = LocalRepository()
        return LoginViewModelFactory(localRepository)
    }
}