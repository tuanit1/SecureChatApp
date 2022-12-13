package com.example.securechatapp.ui.home.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.LocalRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class UserListViewModelFactory(
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val localRepository: LocalRepository
)
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserListViewModel(userRepository, roomRepository, localRepository) as T
    }
}