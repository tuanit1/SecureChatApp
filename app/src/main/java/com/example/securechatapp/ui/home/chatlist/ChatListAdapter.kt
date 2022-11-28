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
import com.example.securechatapp.utils.ChatRoomDiffUtil
import com.squareup.picasso.Picasso

class ChatListAdapter(): RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private var mList: List<ChatRoom> = emptyList()
    var onItemClickListener: (String?) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(
        private val binding: LayoutItemRoomBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatRoom){
            binding.run {

                item.room?.let { room ->
                    if(room.type == Room.GROUP){
                        tvRoomName.text = room.name.decodeBase64()

                        if(room.image.decodeBase64().isNotEmpty()){
                            Picasso.get()
                                .load(room.image.decodeBase64())
                                .placeholder(R.drawable.ic_user_placholder)
                                .into(ivRoom)
                        }else{
                            Picasso.get()
                                .load(R.drawable.ic_user_placholder)
                                .into(ivRoom)
                        }

                        ivGroup.visibility = View.VISIBLE

                    }else{
                        tvRoomName.text = item.participant?.user?.name?.decodeBase64()
                        ivGroup.visibility = View.GONE

                        item.participant?.user?.let { user ->
                            if(user.image.decodeBase64().isNotEmpty()){
                                Picasso.get()
                                    .load(user.image.decodeBase64())
                                    .placeholder(R.drawable.ic_user_placeholder2)
                                    .into(ivRoom)
                            }else{
                                Picasso.get()
                                    .load(R.drawable.ic_user_placeholder2)
                                    .into(ivRoom)
                            }
                        }
                    }

                    if(item.message != null){
                        tvTime.text = item.message?.time?.toFormattedDate()
                        tvRoomLatestMessage.text = item.message?.message?.decodeBase64()
                        tvTime.visibility = View.VISIBLE
                    }else{
                        tvTime.visibility = View.GONE
                        tvRoomLatestMessage.text = itemView.context.getString(R.string.no_message_yet)
                    }

                    clItem.setOnClickListener {
                        onItemClickListener(room.id)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun setData(newList: List<ChatRoom>){
        val diffUtil = ChatRoomDiffUtil(mList, newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        mList = newList.map { it.copy() }
        diffResults.dispatchUpdatesTo(this)
    }

}

