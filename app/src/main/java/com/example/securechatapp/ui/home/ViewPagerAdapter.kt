package com.example.securechatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.ui.home.setting.SettingFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChatListFragment.newInstance(ChatListFragment.GROUP_TYPE)
            1 -> ChatListFragment.newInstance(ChatListFragment.USERS_TYPE)
            2 -> SettingFragment.newInstance()
            else -> SettingFragment.newInstance()
        }
    }
}