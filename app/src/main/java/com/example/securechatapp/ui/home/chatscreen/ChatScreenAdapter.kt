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
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.databinding.LayoutItemMessageLeftBinding
import com.example.securechatapp.databinding.LayoutItemMessageRightBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.getDiffInMinute
import com.example.securechatapp.extension.toFormattedDate
import com.example.securechatapp.utils.Constant

class ChatScreenAdapter() :
    ListAdapter<Message, ChatScreenAdapter.ViewHolder>(MessageDiffCallback()) {

    companion object {
        const val LEFT_MESSAGE = 1
        const val RIGHT_MESSAGE = 2
    }

    inner class ViewHolder(
        private val mBinding: ViewBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: Message) {
            when (mBinding) {
                is LayoutItemMessageLeftBinding -> loadLeftMessage(item)
                is LayoutItemMessageRightBinding -> loadRightMessage(item)
            }
        }

        private fun loadLeftMessage(item: Message) {
            val binding = mBinding as LayoutItemMessageLeftBinding

            binding.run {
                if (currentList.isNotEmpty()) {

                    val layoutParam = clItem.layoutParams as RecyclerView.LayoutParams
                    setItemLayoutParams(layoutParam)

                    if(adapterPosition < currentList.size - 1){
                        val nextItem = getItem(adapterPosition + 1)
                        val diffMin = getDiffInMinute(nextItem.time, item.time)
                        if (nextItem.uid == item.uid && diffMin < 1) {
                            ivUserThumb.visibility = View.INVISIBLE
                        }
                    }

                    if (adapterPosition > 0 && adapterPosition < currentList.size - 1) {
                        val prevItem = getItem(adapterPosition - 1)

                        val diffMin = getDiffInMinute(item.time, prevItem.time)

                        if(diffMin <= 1){
                            if (prevItem.uid == item.uid) {
                                tvNameSender.visibility = View.GONE
                            }
                        }
                    }

                    tvMessageContent.text = item.message.decodeBase64()
                    tvMessageTime.text = item.time.toFormattedDate()
                    tvNameSender.text = item.uid

                }
            }


        }

        private fun loadRightMessage(item: Message) {
            val binding = mBinding as LayoutItemMessageRightBinding

            binding.run {

                val layoutParam = clItem.layoutParams as RecyclerView.LayoutParams
                setItemLayoutParams(layoutParam)

                tvMessageContent.text = item.message.decodeBase64()
                tvMessageTime.text = item.time.decodeBase64().toFormattedDate()
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
                        getItem(adapterPosition).time,
                        getItem(adapterPosition - 1).time
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
        return when (getItem(position).uid) {
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