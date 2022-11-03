package com.example.securechatapp.ui.home.addgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.data.repository.ChatListRepository

@Suppress("UNCHECKED_CAST")
class AddGroupViewModelFactory()
    : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddGroupViewModel() as T
    }
}