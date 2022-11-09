package com.example.securechatapp.ui.home.chatscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.ChatListRepository
import com.example.securechatapp.data.repository.MessageRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.data.repository.UserRepository
import com.example.securechatapp.ui.home.chatlist.ChatListViewModel

@Suppress("UNCHECKED_CAST")
class ChatScreenViewModelFactory(
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository
)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatScreenViewModel(roomRepository, messageRepository) as T
    }
}