package com.example.securechatapp.ui.home.chatsetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.data.model.User
import com.example.securechatapp.data.repository.ParticipantRepository

class ChatSettingViewModel(
    private val participantRepository: ParticipantRepository
) : ViewModel() {
    var mPartyList: MutableLiveData<List<Participant>> = MutableLiveData()

    init {
        loadPartyList()
    }

    private fun loadPartyList() {
        mPartyList.value = generateDummyList()
    }

    private fun generateDummyList() = mutableListOf<Participant>().apply {
        for(i in 1..10){
            add(
                Participant(
                    "id$i",
                    user = User(
                        "uid$i",
                        "Name$i",
                        "Age$i",
                        "t@gmail.com",
                        "https://cdn-icons-png.flaticon.com/512/2202/2202112.png"

                    ),
                    "roomId$i",
                    "okay",
                    "2022-11-05T02:12:07.937Z",
                    false,
                    true,
                    true,
                    true
                )
            )
        }
    }
}