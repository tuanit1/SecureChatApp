package com.example.securechatapp.utils

import android.util.Log
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.Participant
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket

class AppSocket private constructor(){
    var mSocketMessage: Socket = IO.socket("${Constant.SERVER_URL}/message")
    var mSocketParticipant: Socket = IO.socket("${Constant.SERVER_URL}/participant")
    var onListenMessage: (ChatMessage) -> Unit  = {}
    var onListenParticipant: (Participant) -> Unit = {}

    init {
        mSocketMessage.on(ON_MESSAGE) {
            try {
                val jsonString = it[0].toString()
                val chatMessage = Gson().fromJson(jsonString, ChatMessage::class.java)

                onListenMessage(chatMessage)
            }catch (e: Exception){
                Log.e("tuan", e.message.toString())
            }

        }

        mSocketParticipant.on(ON_PARTICIPANT){
            try {
                val jsonString = it[0].toString()
                val participant = Gson().fromJson(jsonString, Participant::class.java)

                onListenParticipant(participant)
            }catch (e: Exception){
                Log.e("tuan", e.message.toString())
            }
        }

        mSocketMessage.on(Socket.EVENT_CONNECT_ERROR){
            Log.e("tuan", "SocketIO Err")
        }

        mSocketMessage.on(Socket.EVENT_CONNECT){
            Log.e("tuan", "SocketIO connected")
        }

        mSocketMessage.on(Socket.EVENT_DISCONNECT){
            Log.e("tuan", "SocketIO disconnected")
        }

    }

    private object Holder { val INSTANCE = AppSocket() }

    companion object {

        const val ON_MESSAGE = "message"
        const val ON_PARTICIPANT = "participant"

        @JvmStatic
        fun getInstance(): AppSocket{
            return Holder.INSTANCE
        }
    }

}