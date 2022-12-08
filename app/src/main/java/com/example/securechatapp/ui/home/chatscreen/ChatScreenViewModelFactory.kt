package com.example.securechatapp.ui.home.chatscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.*
import com.example.securechatapp.ui.home.chatlist.ChatListViewModel

@Suppress("UNCHECKED_CAST")
class ChatScreenViewModelFactory(
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository,
    private val participantRepository: ParticipantRepository
)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatScreenViewModel(roomRepository, messageRepository, participantRepository) as T
    }
}