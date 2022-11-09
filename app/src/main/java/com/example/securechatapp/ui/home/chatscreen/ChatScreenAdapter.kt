package com.example.securechatapp.ui.home.chatscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.databinding.LayoutItemMessageLeftBinding
import com.example.securechatapp.databinding.LayoutItemMessageRightBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.toFormattedDate
import com.example.securechatapp.utils.Constant

class ChatScreenAdapter(): ListAdapter<Message, ChatScreenAdapter.ViewHolder>(MessageDiffCallback()) {

    companion object{
        const val LEFT_MESSAGE = 1
        const val RIGHT_MESSAGE = 2
    }

    inner class ViewHolder(
        private val mBinding: ViewBinding
    ): RecyclerView.ViewHolder(mBinding.root){
        fun bind(item: Message){
            when(mBinding){
                is LayoutItemMessageLeftBinding -> loadLeftMessage(item)
                is LayoutItemMessageRightBinding -> loadRightMessage(item)
            }
        }

        private fun loadLeftMessage(item: Message){
            val binding = mBinding as LayoutItemMessageLeftBinding

            binding.run {
                tvMessageContent.text = item.message.decodeBase64()
                tvMessageTime.text = item.time.toFormattedDate()
                tvNameSender.text = item.uid

            }
        }

        private fun loadRightMessage(item: Message){
            val binding = mBinding as LayoutItemMessageRightBinding

            binding.run {
                tvMessageContent.text = item.message.decodeBase64()
                tvMessageTime.text = item.time.decodeBase64().toFormattedDate()


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return when(viewType){
            LEFT_MESSAGE -> ViewHolder(LayoutItemMessageLeftBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            RIGHT_MESSAGE -> ViewHolder(LayoutItemMessageRightBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
           else -> ViewHolder(LayoutItemMessageLeftBinding
               .inflate(LayoutInflater.from(parent.context), parent, false))
       }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).uid){
            Constant.mUID -> RIGHT_MESSAGE
            else -> LEFT_MESSAGE
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }



}