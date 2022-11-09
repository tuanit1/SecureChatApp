package com.example.securechatapp.ui.home.chatscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.repository.MessageRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.extension.encodeBase64
import com.example.securechatapp.extension.getCurrentFormattedDate
import com.example.securechatapp.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ChatScreenViewModel(
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository
): ViewModel() {

    var mChatRoom: MutableLiveData<ChatRoom> = MutableLiveData()
    var mMessages: MutableLiveData<List<Message>> = MutableLiveData()

    fun loadRoom(roomID: String, callback: APICallback){
        callback.onStart()
        roomRepository.getRoomByID(Constant.mUID, roomID).enqueue(object : retrofit2.Callback<ResponseObject<ChatRoom>>{
            override fun onResponse(
                call: Call<ResponseObject<ChatRoom>>,
                response: Response<ResponseObject<ChatRoom>>
            ) {

                if(response.isSuccessful){
                    if(response.body()?.success == true){
                        response.body()?.data?.let {
                            mChatRoom.postValue(it)
                            callback.onSuccess()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseObject<ChatRoom>>, t: Throwable) {
                Log.d("tuan", "load room of chat screen fail")
                callback.onError(t)
            }

        })
    }

    fun loadMessage(roomID: String, callback: APICallback){
        messageRepository.getMessagesByRoomID(roomID).enqueue(object : Callback<ResponseObject<List<Message>>>{
            override fun onResponse(
                call: Call<ResponseObject<List<Message>>>,
                response: Response<ResponseObject<List<Message>>>
            ) {
                if(response.isSuccessful && response.body()?.success == true){
                    response.body()?.data?.let { list ->
                        mMessages.postValue(list)
                        callback.onSuccess()
                    }
                }else{
                    callback.onError()
                }
            }

            override fun onFailure(call: Call<ResponseObject<List<Message>>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun sendTextMessage(text: String, roomID: String){
        val id = UUID.randomUUID().toString()
        val message = Message(id, text.encodeBase64(), getCurrentFormattedDate(), Message.TEXT, Constant.mUID, roomID)

        val newList = mMessages.value?.toMutableList()?.apply { add(message) }
        mMessages.postValue(newList?.toList())
    }

}