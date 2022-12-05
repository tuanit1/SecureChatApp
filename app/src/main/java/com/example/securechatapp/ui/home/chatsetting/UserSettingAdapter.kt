package com.example.securechatapp.ui.home.chatsetting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.R
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.LayoutItemSettingUserBinding
import com.example.securechatapp.extension.decodeBase64
import com.squareup.picasso.Picasso

class UserSettingAdapter: ListAdapter<Participant, UserSettingAdapter.ViewHolder>(ParticipantDiffCallback()) {

    var onItemClick: (String) -> Unit = {}

    inner class ViewHolder(
        private val binding: LayoutItemSettingUserBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Participant){
            binding.run {
                tvName.text = item.user.name.decodeBase64()

                root.setOnClickListener {
                    onItemClick(item.user.uid)
                }

                if(item.user.image.decodeBase64().isEmpty()){
                    Picasso.get()
                        .load(R.drawable.ic_user_placholder)
                        .into(ivUser)
                }else{
                    Picasso.get()
                        .load(item.user.image.decodeBase64())
                        .placeholder(R.drawable.ic_user_placholder)
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