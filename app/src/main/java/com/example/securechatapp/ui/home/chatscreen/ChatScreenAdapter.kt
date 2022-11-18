package com.example.securechatapp.ui.home.chatscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.securechatapp.R
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.databinding.LayoutItemMessageLeftBinding
import com.example.securechatapp.databinding.LayoutItemMessageRightBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.getDiffInMinute
import com.example.securechatapp.extension.toFormattedDate
import com.example.securechatapp.utils.Constant
import com.squareup.picasso.Picasso

class ChatScreenAdapter() :
    ListAdapter<ChatMessage, ChatScreenAdapter.ViewHolder>(ChatMessageDiffCallback()) {

    companion object {
        const val LEFT_MESSAGE = 1
        const val RIGHT_MESSAGE = 2
    }

    inner class ViewHolder(
        private val mBinding: ViewBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ChatMessage) {
            when (mBinding) {
                is LayoutItemMessageLeftBinding -> loadLeftMessage(item)
                is LayoutItemMessageRightBinding -> loadRightMessage(item)
            }
        }

        private fun loadLeftMessage(chatMessage: ChatMessage) {
            val binding = mBinding as LayoutItemMessageLeftBinding

            binding.run {
                if (currentList.isNotEmpty()) {

                    val layoutParam = clItem.layoutParams as RecyclerView.LayoutParams
                    setItemLayoutParams(layoutParam)

                    chatMessage.message.let { item ->

                        if(adapterPosition < currentList.size - 1){
                            val nextItem = getItem(adapterPosition + 1)
                            val diffMin = getDiffInMinute(nextItem.message.time, item.time)
                            if (nextItem.message.uid == item.uid && diffMin < 1) {
                                ivUserThumb.visibility = View.INVISIBLE
                            }
                        }

                        if (adapterPosition > 0 && adapterPosition < currentList.size) {
                            val prevItem = getItem(adapterPosition - 1)

                            val diffMin = getDiffInMinute(item.time, prevItem.message.time)

                            if(diffMin <= 1){
                                if (prevItem.message.uid == item.uid) {
                                    tvNameSender.visibility = View.GONE
                                }
                            }
                        }

                        tvMessageContent.text = item.message.decodeBase64()
                        tvMessageTime.text = item.time.toFormattedDate()
                        tvNameSender.text = item.uid
                        tvNameSender.text = chatMessage.participant.user.name.decodeBase64()
                    }

                    val url = chatMessage.participant.user.image.decodeBase64()
                    if(url.isNotEmpty()){
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_user_placholder)
                            .into(ivUserThumb)
                    }else{
                        Picasso.get()
                            .load(R.drawable.ic_user_placholder)
                            .into(ivUserThumb)
                    }

                }
            }

        }

        private fun loadRightMessage(chatMessage: ChatMessage) {
            val binding = mBinding as LayoutItemMessageRightBinding

            binding.run {

                val layoutParam = clItem.layoutParams as RecyclerView.LayoutParams
                setItemLayoutParams(layoutParam)

                chatMessage.message.let { item ->
                    tvMessageContent.text = item.message.decodeBase64()
                    tvMessageTime.text = item.time.decodeBase64().toFormattedDate()
                }

            }
        }

        private fun setItemLayoutParams(layoutParam: RecyclerView.LayoutParams) {
            val dim5dp = itemView.context.resources.getDimension(R.dimen._5dp).toInt()
            val dim10dp = itemView.context.resources.getDimension(R.dimen._10dp).toInt()
            val dim20dp = itemView.context.resources.getDimension(R.dimen._20dp).toInt()
            layoutParam.run {

                if(adapterPosition == 0){
                    topMargin = dim10dp
                }else if(adapterPosition > 0 && adapterPosition < currentList.size){
                    val diffMin = getDiffInMinute(
                        getItem(adapterPosition).message.time,
                        getItem(adapterPosition - 1).message.time
                    )

                    topMargin = if(diffMin < 1){
                        dim5dp
                    }else{
                        dim20dp
                    }
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            LEFT_MESSAGE -> ViewHolder(
                LayoutItemMessageLeftBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            RIGHT_MESSAGE -> ViewHolder(
                LayoutItemMessageRightBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ViewHolder(
                LayoutItemMessageLeftBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).participant.user.uid) {
            Constant.mUID -> RIGHT_MESSAGE
            else -> LEFT_MESSAGE
        }
    }

    class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

    }


}