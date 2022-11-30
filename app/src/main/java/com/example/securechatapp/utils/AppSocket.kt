package com.example.securechatapp.utils

import android.util.Log
import com.example.securechatapp.data.model.ChatMessage
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket

class AppSocket private constructor(){

    var mSocket: Socket = IO.socket("http://${Constant.SERVER_URL}:8080/message")
    var onListenMessage: (ChatMessage) -> Unit  = {}

    init {
        mSocket.on(ON_MESSAGE) {

            try {
                val jsonString = it[0].toString()
                val chatMessage = Gson().fromJson(jsonString, ChatMessage::class.java)

                onListenMessage(chatMessage)
            }catch (e: Exception){
                Log.e("tuan", e.message.toString())
            }

        }

        mSocket.on(Socket.EVENT_CONNECT_ERROR){
            Log.e("tuan", "SocketIO Err")
        }

        mSocket.on(Socket.EVENT_CONNECT){
            Log.e("tuan", "SocketIO connected")
        }

        mSocket.on(Socket.EVENT_DISCONNECT){
            Log.e("tuan", "SocketIO disconnected")
        }

    }

    private object Holder { val INSTANCE = AppSocket() }

    companion object {

        const val ON_MESSAGE = "message"

        @JvmStatic
        fun getInstance(): AppSocket{
            return Holder.INSTANCE
        }
    }

}