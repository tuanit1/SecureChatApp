package com.example.securechatapp.ui.home.chatscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.securechatapp.R
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.databinding.FragmentChatScreenBinding
import com.example.securechatapp.extension.encodeBase64
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.utils.Constant


class ChatScreenFragment : Fragment() {

    companion object {
        const val ROOM_ID = "room_id"
        fun newInstance(roomId: String) = ChatScreenFragment().apply {
            arguments = Bundle().apply {
                putString(ROOM_ID, roomId)
            }
        }
    }

    private var mRoomID: String? = null

    private var binding: FragmentChatScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatScreenBinding.inflate(inflater)

        mRoomID = arguments?.getString(ROOM_ID)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frg = parentFragmentManager.findFragmentByTag(HomeFragment::class.java.name) as HomeFragment
        val chatListFragment = frg.mAdapter?.mFragments?.get(0) as ChatListFragment

        binding?.run {
            btnSend.setOnClickListener {
                if(edtMessage.text.isNotEmpty()){
                    val text = edtMessage.text.toString()

                    mRoomID?.let { mRoomID ->
                        val message = Message("aaa", text.encodeBase64(), "2022-11-05T02:12:07.937Z", "text", Constant.mUID,
                            mRoomID
                        )
                        chatListFragment.addMessage(message)
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        }
    }


}