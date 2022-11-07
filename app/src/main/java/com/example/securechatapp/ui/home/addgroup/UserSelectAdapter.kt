package com.example.securechatapp.ui.home.addgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.R
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.LayoutItemSelectUserBinding
import com.example.securechatapp.extension.decodeBase64
import com.squareup.picasso.Picasso

class UserListAdapter(
    private var mList: MutableList<User>
): ListAdapter<User, UserListAdapter.ViewHolder>(UserDiffCallback()) {

    lateinit var onCheckUser: (Int, Boolean) -> Unit

    inner class ViewHolder(
        private val binding: LayoutItemSelectUserBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: User){
            binding.run {
                tvName.text = item.name.decodeBase64()
                tvPhone.text = item.phone.decodeBase64()
                checkBox.isChecked = item.isSelected ?: false

                if(item.image.decodeBase64().isEmpty()){
                    Picasso.get()
                        .load(R.drawable.ic_user_placholder)
                        .into(ivUser)
                }else{
                    Picasso.get()
                        .load(item.image.decodeBase64())
                        .placeholder(R.drawable.ic_user_placholder)
                        .into(ivUser)
                }

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onCheckUser(adapterPosition, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemSelectUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

}