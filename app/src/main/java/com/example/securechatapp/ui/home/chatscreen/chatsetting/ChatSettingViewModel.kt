package com.example.securechatapp.ui.home.chatscreen.chatsetting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.ParticipantRepository
import com.example.securechatapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatSettingViewModel(
    private val participantRepository: ParticipantRepository
) : ViewModel() {
    var mPartyList: MutableLiveData<List<Participant>> = MutableLiveData()
    var mParticipant: MutableLiveData<Participant> = MutableLiveData()
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadPartyList(roomID: String, callback: APICallback) {
        callback.onStart()
        participantRepository.getRoomParticipants(roomID)
            .enqueue(object : Callback<ResponseObject<List<Participant>>> {
                override fun onResponse(
                    call: Call<ResponseObject<List<Participant>>>,
                    response: Response<ResponseObject<List<Participant>>>
                ) {
                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    response.body()?.data?.let { list ->
                                        mPartyList.value = list
                                        mParticipant.value = list.find { it.user.uid == Constant.mUID }
                                    }
                                }

                                callback.onSuccess()
                            },
                            onTokenUpdated = {
                                loadPartyList(roomID, callback)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = { callback.onError() }
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ResponseObject<List<Participant>>>,
                    t: Throwable
                ) {
                    Log.e("tuan", t.message.toString())
                    callback.onError()
                }

            })
    }

    fun updateParticipant(participant: Participant, callback: APICallback) {
        val body = HashMap<String, String>().apply {
            set("nickname", participant.nickname)
            set("isAdmin", participant.isAdmin.toString())
            set("allowSendMSG", participant.allowSendMSG.toString())
            set("allowSendFile", participant.allowSendFile.toString())
            set("allowViewFile", participant.allowViewFile.toString())
        }

        participantRepository.updateParticipant(participant.id, body)
            .enqueue(object : Callback<ResponseObject<Any>> {
                override fun onResponse(
                    call: Call<ResponseObject<Any>>,
                    response: Response<ResponseObject<Any>>
                ) {

                    callback.onStart()

                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    callback.onSuccess()
                                } else {
                                    callback.onError()
                                }
                            },
                            onTokenUpdated = {
                                updateParticipant(participant, callback)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = { callback.onError() }
                        )
                    }

                }

                override fun onFailure(call: Call<ResponseObject<Any>>, t: Throwable) {
                    Log.e("tuan", t.message.toString())
                    callback.onError()
                }

            })
    }

    fun handleParticipantUpdateSocket(participant: Participant) {

         val updatedList = mPartyList.value?.map {
             if (it.id == participant.id) participant else it
         }

        mPartyList.postValue(updatedList?.toMutableList())


    }

}