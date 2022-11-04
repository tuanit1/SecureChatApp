package com.example.securechatapp.ui.home.chatlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.databinding.FragmentChatListBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.ui.home.addgroup.AddGroupFragment
import com.example.securechatapp.utils.Constant
import com.example.securechatapp.utils.InjectorUtils


class ChatListFragment : Fragment() {

    private var binding: FragmentChatListBinding? = null
    private var mViewModel: ChatListViewModel? = null
    private var mType: String? = null
    private var mList: MutableList<ChatRoom>? = null
    private var mAdapter: ChatListAdapter? = null

    companion object {

        const val GROUP_TYPE = "GROUP_TYPE"
        const val USERS_TYPE = "USERS_TYPE"

        fun newInstance(type: String) = ChatListFragment().apply {
            arguments?.putString("type", type)
        }
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
        initView()
        initListener()
    }

    private fun initView() {
        mType = arguments?.getString("type")
        mList = mutableListOf()
        mList?.let { mList ->
            mAdapter = ChatListAdapter(mList)
        }

        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context)
            rv.itemAnimator = DefaultItemAnimator()
        }

        val factory = InjectorUtils.provideChatListViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[ChatListViewModel::class.java]
        mViewModel?.loadRoomList(Constant.mUID, object : APICallback{
            override fun onStart() {
                binding?.progressBar?.visibility = View.VISIBLE
            }

            override fun onSuccess() {
                binding?.progressBar?.visibility = View.GONE
            }

            override fun onError(t: Throwable?) {
                binding?.progressBar?.visibility = View.GONE
            }

        })
    }

    private fun initListener() {
        mViewModel?.run {
            mChatRooms.observe(viewLifecycleOwner){ mChatRooms ->
                mList?.clear()
                mList?.addAll(mChatRooms)
                mAdapter?.submitList(mList?.toMutableList())
            }
        }

        binding?.run {
            ivAddGroup.setOnClickListener {

                val addGroupFragment = AddGroupFragment.newInstance().apply {
                    onDoneListener = { isSuccess ->
                        if(isSuccess){
                            mViewModel?.loadRoomList(Constant.mUID)
                        }
                    }
                }

                parentFragment?.addFragment(
                    R.id.fragmentContainerView,
                    addGroupFragment,
                    true,
                    AddGroupFragment::class.java.name
                )
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        Log.e("tuan", "${this.javaClass.name}: onResume")
//    }
//
//    override fun onStart() {
//        super.onStart()
//        Log.e("tuan", "${this.javaClass.name}: onStart")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.e("tuan", "${this.javaClass.name}: onPause")
//
//    }

}