package com.example.securechatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.ui.home.setting.SettingFragment
import com.example.securechatapp.ui.home.userlist.UserListFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val mFragments = mutableListOf(
        ChatListFragment.newInstance(),
        UserListFragment.newInstance(),
        SettingFragment.newInstance()
    )

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

}