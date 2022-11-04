package com.example.securechatapp.ui.home.chatlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.R
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.databinding.LayoutItemRoomBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.toFormattedDate
import com.squareup.picasso.Picasso

class ChatListAdapter(
    private var mList: MutableList<ChatRoom>
): ListAdapter<ChatRoom, ChatListAdapter.ViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(
        private val binding: LayoutItemRoomBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatRoom){
            binding.run {
                if(item.room.type == Room.GROUP){
                    tvRoomName.text = item.room.name.decodeBase64()

                    if(item.room.image.decodeBase64().isNotEmpty()){
                        Picasso.get()
                            .load(item.room.image.decodeBase64())
                            .placeholder(R.drawable.ic_user_placholder)
                            .into(ivRoom)
                    }

                }else{
                    tvRoomName.text = item.user.name.decodeBase64()
                }

                if(item.messages.isNotEmpty()){
                    tvTime.text = item.messages.last().time.toFormattedDate()
                    tvRoomLatestMessage.text = item.messages.last().message.decodeBase64()
                    tvTime.visibility = View.VISIBLE
                }else{
                    tvTime.visibility = View.GONE
                    tvRoomLatestMessage.text = "No message yet"
                }

            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
}

class RoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem == newItem
    }

}