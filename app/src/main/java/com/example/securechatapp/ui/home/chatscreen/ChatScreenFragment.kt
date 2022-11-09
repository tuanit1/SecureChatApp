package com.example.securechatapp.ui.home.chatscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.databinding.FragmentChatScreenBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.utils.InjectorUtils
import com.squareup.picasso.Picasso


class ChatScreenFragment : Fragment() {

    companion object {
        const val ROOM_ID = "room_id"
        fun newInstance(roomId: String) = ChatScreenFragment().apply {
            arguments = Bundle().apply {
                putString(ROOM_ID, roomId)
            }
        }
    }

    private var isEmptyInput = true
    private var mRoomID: String? = null
    private var binding: FragmentChatScreenBinding? = null
    private var chatListFragment: ChatListFragment? = null
    private var mViewModel: ChatScreenViewModel? = null
    private var mAdapter: ChatScreenAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatScreenBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initListener() {
        binding?.run {
            edtMessage.addTextChangedListener { text ->
                if(text?.isNotEmpty() == true){
                    isEmptyInput = false
                    ivSendMessage.isSelected = true
                }else{
                    isEmptyInput = true
                    ivSendMessage.isSelected = false
                }
            }

            ivBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            ivSendMessage.setOnClickListener {
                val b = isEmptyInput
                if(!isEmptyInput){
                    mRoomID?.let {
                        mViewModel?.sendTextMessage(edtMessage.text.toString(), it)
                        edtMessage.text.clear()
                    }
                }
            }

        }

        observeRoom()
        observeMessages()
    }

    private fun observeMessages() {
        binding?.run {
            mViewModel?.mMessages?.observe(viewLifecycleOwner){ list ->
                mAdapter?.submitList(list)
                binding?.rv?.smoothScrollToPosition(list.size - 1)
            }
        }
    }


    private fun observeRoom() {
        binding?.run {
            mViewModel?.mChatRoom?.observe(viewLifecycleOwner){ chatRoom ->
                when(chatRoom.room?.type){
                    Room.GROUP -> run {
                        tvRoomName.text = chatRoom?.room?.name?.decodeBase64()
                        tvType.text = "@${Room.GROUP}"

                        chatRoom?.room?.image?.decodeBase64()?.let { url ->
                            if (url.isNotEmpty()){
                                Picasso.get()
                                    .load(url)
                                    .placeholder(R.drawable.ic_user_placholder)
                                    .into(ivThumb)
                            }else{
                                Picasso.get()
                                    .load(R.drawable.ic_user_placholder)
                                    .into(ivThumb)
                            }
                        }

                    }
                    Room.PRIVATE -> run {
                        tvRoomName.text = chatRoom?.participant?.user?.name?.decodeBase64()
                        tvType.text = "@${Room.PRIVATE}"

                        chatRoom?.participant?.user?.image?.decodeBase64()?.let { url ->
                            if (url.isNotEmpty()){
                                Picasso.get()
                                    .load(url)
                                    .placeholder(R.drawable.ic_user_placeholder2)
                                    .into(ivThumb)
                            }else{
                                Picasso.get()
                                    .load(R.drawable.ic_user_placeholder2)
                                    .into(ivThumb)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        mRoomID = arguments?.getString(ROOM_ID)
        val frg = parentFragmentManager.findFragmentByTag(HomeFragment::class.java.name) as HomeFragment
        chatListFragment = frg.mAdapter?.mFragments?.get(0) as ChatListFragment

        val factory = InjectorUtils.provideChatScreenViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[ChatScreenViewModel::class.java]

        mAdapter = ChatScreenAdapter()

        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
            rv.itemAnimator = DefaultItemAnimator()
        }

        loadChatRoom()
        loadMessages()
    }

    private fun loadMessages() {
        mRoomID?.let { mViewModel?.loadMessage(it, callback = object : APICallback{
            override fun onStart() {
                binding?.progressBarRV?.visibility = View.VISIBLE
            }

            override fun onSuccess(data: Any?) {
                binding?.progressBarRV?.visibility = View.GONE
            }

            override fun onError(t: Throwable?) {
                binding?.progressBarRV?.visibility = View.GONE
                Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_SHORT).show()
            }

        })}
    }

    private fun loadChatRoom() {
        mRoomID?.let { mViewModel?.loadRoom(it, callback = object : APICallback{
            override fun onStart() {
                binding?.titleProgressbar?.visibility = View.VISIBLE
                binding?.clTitle?.visibility = View.GONE
            }

            override fun onSuccess(data: Any?) {
                binding?.titleProgressbar?.visibility = View.GONE
                binding?.clTitle?.visibility = View.VISIBLE
            }

            override fun onError(t: Throwable?) {
                binding?.titleProgressbar?.visibility = View.GONE
                binding?.clTitle?.visibility = View.VISIBLE
            }
        }) }
    }

    private fun handleSendClick(){

//        binding?.run {
//            if(edtMessage.text.isNotEmpty()){
//                val text = edtMessage.text.toString()
//
//                mRoomID?.let { mRoomID ->
//                    val message = Message("aaa", text.encodeBase64(), "2022-11-05T02:12:07.937Z", "text", Constant.mUID,
//                        mRoomID
//                    )
//                    chatListFragment?.addMessage(message)
//                    parentFragmentManager.popBackStack()
//                }
//            }
//        }


    }


}