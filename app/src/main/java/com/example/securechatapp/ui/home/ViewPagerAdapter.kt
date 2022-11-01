package com.example.securechatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.ui.home.setting.SettingFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChatListFragment.newInstance()
            1 -> SettingFragment.newInstance()
            else -> ChatListFragment.newInstance()
        }
    }
}