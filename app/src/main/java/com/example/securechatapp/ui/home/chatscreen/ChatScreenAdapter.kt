package com.example.securechatapp.ui.home.chatscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.securechatapp.R
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.LayoutItemMessageLeftBinding
import com.example.securechatapp.databinding.LayoutItemMessageRightBinding
import com.example.securechatapp.databinding.LayoutMessageFileBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.getDiffInMinute
import com.example.securechatapp.extension.toFormattedDate
import com.example.securechatapp.utils.Constant
import com.squareup.picasso.Picasso

class ChatScreenAdapter: ListAdapter<ChatMessage, ChatScreenAdapter.ViewHolder>(ChatMessageDiffCallback()) {

    companion object {
        const val LEFT_MESSAGE = 1
        const val RIGHT_MESSAGE = 2
    }

    var onDownloadClickListener: (
        String,
        String,
        ProgressBar,
        ImageView
    ) -> Unit = {_,_,_,_ ->}

    private var isShowFile: Boolean = true

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
                        tvMessageTime.text = item.time.toFormattedDate()
                        tvNameSender.text = item.uid
                        tvNameSender.text = chatMessage.participant.user.name.decodeBase64()

                        when(chatMessage.message.type){
                            Message.TEXT -> {
                                ivMessage.visibility = View.GONE
                                tvMessageContent.visibility = View.VISIBLE
                                layoutFile.root.visibility = View.GONE
                                tvMessageContent.text = item.message.decodeBase64()
                            }

                            Message.IMAGE -> {
                                ivMessage.visibility = View.VISIBLE
                                tvMessageContent.visibility = View.GONE
                                layoutFile.root.visibility = View.GONE

                                val urlImage = item.message.decodeBase64()


                                if(!isShowFile){
                                    Picasso.get()
                                        .load(R.drawable.img_not_allow)
                                        .into(ivMessage)
                                } else if(urlImage.isNotEmpty()){
                                    Picasso.get()
                                        .load(urlImage)
                                        .placeholder(R.drawable.img_photo_placeholder)
                                        .into(ivMessage)
                                }else{
                                    Picasso.get()
                                        .load(R.drawable.img_photo_placeholder)
                                        .into(ivMessage)
                                }
                            }

                            Message.FILE -> {
                                layoutFile.root.visibility = View.VISIBLE
                                tvMessageContent.visibility = View.GONE
                                ivMessage.visibility = View.GONE

                                handleFileMessage(layoutFile, item)
                            }
                        }
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
                    tvMessageTime.text = item.time.decodeBase64().toFormattedDate()
                    when(chatMessage.message.type){
                        Message.TEXT -> {
                            ivMessage.visibility = View.GONE
                            layoutFile.root.visibility = View.GONE
                            tvMessageContent.visibility = View.VISIBLE
                            tvMessageContent.text = item.message.decodeBase64()
                        }

                        Message.IMAGE -> {
                            ivMessage.visibility = View.VISIBLE
                            tvMessageContent.visibility = View.GONE
                            layoutFile.root.visibility = View.GONE

                            val urlImage = item.message.decodeBase64()
                            if(!isShowFile){
                                Picasso.get()
                                    .load(R.drawable.img_not_allow)
                                    .into(ivMessage)
                            } else if(urlImage.isNotEmpty()){
                                Picasso.get()
                                    .load(urlImage)
                                    .placeholder(R.drawable.img_photo_placeholder)
                                    .into(ivMessage)
                            }else{
                                Picasso.get()
                                    .load(R.drawable.img_photo_placeholder)
                                    .into(ivMessage)
                            }
                        }

                        Message.FILE -> {

                            tvMessageContent.visibility = View.GONE
                            ivMessage.visibility = View.GONE
                            layoutFile.root.visibility = View.VISIBLE
                            handleFileMessage(layoutFile, item)
                        }
                    }
                }

            }
        }

        private fun handleFileMessage(
            layoutFile: LayoutMessageFileBinding,
            item: Message
        ){
            layoutFile.run {

                if(isShowFile){
                    tvFileName.text = item.message.decodeBase64()
                    ivDownload.visibility =  View.VISIBLE
                }else{
                    tvFileName.text = itemView.context.getString(R.string.str_not_allow_view_file)
                    ivDownload.visibility =  View.GONE
                }

                progressBar.visibility = View.GONE
                ivDownload.isSelected = item.isDownloaded
                ivDownload.setOnClickListener {
                    if(!item.isDownloaded && isShowFile){
                        onDownloadClickListener(
                            item.id,
                            item.message.decodeBase64(),
                            progressBar,
                            ivDownload
                        )
                    }
                }
            }
        }


        private fun setItemLayoutParams(layoutParam: RecyclerView.LayoutParams) {
            val dim2dp = itemView.context.resources.getDimension(R.dimen._2dp).toInt()
            val dim10dp = itemView.context.resources.getDimension(R.dimen._10dp).toInt()

            layoutParam.run {

                if(adapterPosition == 0){
                    topMargin = dim10dp
                }else if(adapterPosition > 0 && adapterPosition < currentList.size){
                    val diffMin = getDiffInMinute(
                        getItem(adapterPosition).message.time,
                        getItem(adapterPosition - 1).message.time
                    )

                    topMargin = if(diffMin < 1){
                        dim2dp
                    }else{
                        dim10dp
                    }
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setShowFile(participant: Participant){

        if(participant.allowViewFile != isShowFile){
            isShowFile = participant.allowViewFile

            currentList.forEachIndexed { index, chatMessage ->
                if (chatMessage.message.type == Message.IMAGE || chatMessage.message.type == Message.FILE) {
                    notifyItemChanged(index)
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
            return oldItem.message.id == newItem.message.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

    }


}