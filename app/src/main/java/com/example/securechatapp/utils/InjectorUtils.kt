package com.example.securechatapp.utils

import android.content.Context
import com.example.securechatapp.data.repository.*
import com.example.securechatapp.ui.auth.login.LoginViewModelFactory
import com.example.securechatapp.ui.home.addgroup.AddGroupViewModelFactory
import com.example.securechatapp.ui.home.chatlist.ChatListViewModelFactory
import com.example.securechatapp.ui.home.chatscreen.ChatScreenViewModelFactory
import com.example.securechatapp.ui.home.chatscreen.chatsetting.ChatSettingViewModel
import com.example.securechatapp.ui.home.chatscreen.chatsetting.ChatSettingViewModelFactory
import com.example.securechatapp.ui.home.setting.SettingViewModelFactory
import com.example.securechatapp.ui.home.setting.setuppin.SetupPinViewModel
import com.example.securechatapp.ui.home.setting.setuppin.SetupPinViewModelFactory
import com.example.securechatapp.ui.home.userlist.UserListViewModelFactory

object InjectorUtils {
    fun provideChatListViewModelFactory(context: Context): ChatListViewModelFactory {
        val chatRepository = ChatListRepository()
        val userRepository = UserRepository()
        val localRepository = LocalRepository(context)
        return ChatListViewModelFactory(chatRepository, userRepository, localRepository)
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
        val participantRepository = ParticipantRepository()

        return ChatScreenViewModelFactory(roomRepository, messageRepository, participantRepository)
    }

    fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
        val localRepository = LocalRepository(context)
        return LoginViewModelFactory(localRepository)
    }

    fun provideChatSettingViewModelFactory(): ChatSettingViewModelFactory {
        val participantRepository = ParticipantRepository()
        return ChatSettingViewModelFactory(participantRepository)
    }

    fun provideSettingViewModelFactory(context: Context): SettingViewModelFactory {
        val userRepository = UserRepository()
        val localRepository = LocalRepository(context)
        return SettingViewModelFactory(userRepository, localRepository)
    }

    fun provideSetupPinViewModelFactory(context: Context): SetupPinViewModelFactory {
        val localRepository = LocalRepository(context)
        return SetupPinViewModelFactory(localRepository)
    }
}