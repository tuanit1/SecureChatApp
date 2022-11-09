package com.example.securechatapp.ui.home.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.R
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.LayoutItemUserBinding
import com.example.securechatapp.extension.decodeBase64
import com.squareup.picasso.Picasso

class UserListAdapter(): ListAdapter<User, UserListAdapter.ViewHolder>(UserDiffCallback()) {

    var onItemClickListener: (String) -> Unit = {}

    inner class ViewHolder(
        private val binding: LayoutItemUserBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: User){
            binding.run {
                tvName.text = item.name.decodeBase64()
                tvPhone.text = item.phone.decodeBase64()

                if(item.image.decodeBase64().isEmpty()){
                    Picasso.get()
                        .load(R.drawable.ic_user_placholder)
                        .into(ivUser)
                }else{
                    Picasso.get()
                        .load(item.image.decodeBase64())
                        .into(ivUser)
                }

                clItem.setOnClickListener {
                    onItemClickListener(item.uid)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}

