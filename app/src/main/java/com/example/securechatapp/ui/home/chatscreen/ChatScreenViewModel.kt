package com.example.securechatapp.ui.home.chatscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatMessage
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
    var mMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    private var mPage = 0
    private var mStep = 5

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

    fun fetchMessage(roomID: String, callback: APICallback){
        messageRepository.getMessagesByRoomID(roomID, mPage, mStep).enqueue(object : Callback<ResponseObject<List<ChatMessage>>>{
            override fun onResponse(
                call: Call<ResponseObject<List<ChatMessage>>>,
                response: Response<ResponseObject<List<ChatMessage>>>
            ) {
                if(response.isSuccessful && response.body()?.success == true){
                    response.body()?.data?.let { list ->
                        mMessages.postValue(list)
                        callback.onSuccess()

                        mPage++
                    }
                }else{
                    callback.onError()
                }
            }

            override fun onFailure(call: Call<ResponseObject<List<ChatMessage>>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun sendTextMessage(text: String, roomID: String){

        val body = HashMap<String, String>().apply {
            set("message", text.encodeBase64())
            set("type", Message.TEXT)
        }

        messageRepository.createMessage(Constant.mUID, roomID, body).enqueue(object : Callback<ResponseObject<Message>>{
            override fun onResponse(
                call: Call<ResponseObject<Message>>,
                response: Response<ResponseObject<Message>>
            ) {
                if(response.isSuccessful && response.body()?.success == true){
                    response.body()?.data?.let { message ->
                        Log.e("tuan", "send message successful ${message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseObject<Message>>, t: Throwable) {
                Log.e("tuan", t.toString())
            }

        })

    }

}