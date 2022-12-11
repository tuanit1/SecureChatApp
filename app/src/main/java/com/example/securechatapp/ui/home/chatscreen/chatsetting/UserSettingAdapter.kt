package com.example.securechatapp.ui.home.chatscreen.chatsetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.R
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.LayoutItemSettingUserBinding
import com.example.securechatapp.extension.decodeBase64
import com.squareup.picasso.Picasso

class UserSettingAdapter: ListAdapter<Participant, UserSettingAdapter.ViewHolder>(
    ParticipantDiffCallback()
) {

    var onItemClick: (Participant) -> Unit = {}

    inner class ViewHolder(
        private val binding: LayoutItemSettingUserBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Participant){
            binding.run {
                tvName.text = item.user.name.decodeBase64()

                if(item.isAdmin){
                    ivEdit.visibility = View.GONE
                    ivAdmin.visibility = View.VISIBLE
                }else{
                    ivEdit.visibility = View.VISIBLE
                    ivAdmin.visibility = View.GONE
                }

                ivEdit.setOnClickListener {
                    if (!item.isAdmin) {
                        onItemClick(item)
                    }

                }

                var count = 0

                if(item.allowViewFile){
                    count++
                }
                if(item.allowSendFile){
                    count++
                }
                if(item.allowSendMSG){
                    count++
                }

                tvState.text = buildString {
                    append(count)
                    append("/3 privileges")
                }


                if(item.user.image.decodeBase64().isEmpty()){
                    Picasso.get()
                        .load(R.drawable.ic_user_placeholder2)
                        .into(ivUser)
                }else{
                    Picasso.get()
                        .load(item.user.image.decodeBase64())
                        .placeholder(R.drawable.ic_user_placeholder2)
                        .into(ivUser)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemSettingUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ParticipantDiffCallback : DiffUtil.ItemCallback<Participant>() {
    override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
        return oldItem == newItem
    }

}