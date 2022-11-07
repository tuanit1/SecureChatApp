package com.example.securechatapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.securechatapp.data.model.ChatRoom

class ChatRoomDiffUtil(
    private val oldList: List<ChatRoom>,
    private val newList: List<ChatRoom>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].room?.id == newList[newItemPosition].room?.id

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return old == new
    }
}