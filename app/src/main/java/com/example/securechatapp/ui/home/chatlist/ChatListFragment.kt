package com.example.securechatapp.ui.home.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentChatListBinding


class ChatListFragment : Fragment() {

    private var binding: FragmentChatListBinding? = null

    companion object {
        fun newInstance() = ChatListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}