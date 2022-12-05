package com.example.securechatapp.utils

import android.content.Context
import com.example.securechatapp.data.repository.*
import com.example.securechatapp.ui.auth.login.LoginViewModelFactory
import com.example.securechatapp.ui.home.addgroup.AddGroupViewModelFactory
import com.example.securechatapp.ui.home.chatlist.ChatListViewModelFactory
import com.example.securechatapp.ui.home.chatscreen.ChatScreenViewModelFactory
import com.example.securechatapp.ui.home.chatsetting.ChatSettingViewModel
import com.example.securechatapp.ui.home.chatsetting.ChatSettingViewModelFactory
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

    fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
        val localRepository = LocalRepository(context)
        return LoginViewModelFactory(localRepository)
    }

    fun provideChatSettingViewModelFactory(): ChatSettingViewModelFactory{
        val participantRepository = ParticipantRepository()
        return ChatSettingViewModelFactory(participantRepository)
    }
}