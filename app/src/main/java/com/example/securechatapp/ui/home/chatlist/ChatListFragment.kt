package com.example.securechatapp.ui.home.chatlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.FragmentChatListBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.ui.home.addgroup.AddGroupFragment
import com.example.securechatapp.ui.home.chatscreen.ChatScreenFragment
import com.example.securechatapp.utils.Constant
import com.example.securechatapp.utils.InjectorUtils
import com.example.securechatapp.widget.EnterPinDialog
import com.squareup.picasso.Picasso


class ChatListFragment : Fragment() {

    private var binding: FragmentChatListBinding? = null
    private var mViewModel: ChatListViewModel? = null
    private var mAdapter: ChatListAdapter? = null

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
        initView()
        initListener()
    }

    private fun initView() {
        mAdapter = ChatListAdapter()

        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context)
            rv.itemAnimator = DefaultItemAnimator()
        }

        val factory = InjectorUtils.provideChatListViewModelFactory(requireContext())
        mViewModel = ViewModelProvider(this, factory)[ChatListViewModel::class.java]

        loadList()
        loadUserImage()
    }

    private fun loadUserImage() {
        mViewModel?.getCurrentUser(Constant.mUID, object : APICallback {
            override fun onStart() = Unit

            override fun onSuccess(data: Any?) {
                if (data is User) {
                    val url = data.image.decodeBase64()

                    if (url.isNotEmpty()) {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    } else {
                        Picasso.get()
                            .load(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("tuan", "fail load user image: ${t?.message}")
            }

        })
    }

    private fun loadList() {
        mViewModel?.loadRoomList(Constant.mUID, object : APICallback {
            override fun onStart() {
                binding?.progressBar?.visibility = View.VISIBLE
            }

            override fun onSuccess(data: Any?) {
                binding?.progressBar?.visibility = View.GONE
                binding?.swipeRefreshLayout?.isRefreshing = false
            }

            override fun onError(t: Throwable?) {
                binding?.progressBar?.visibility = View.GONE
                binding?.swipeRefreshLayout?.isRefreshing = false
            }

        })
    }

    private fun initListener() {
        mViewModel?.run {
            mChatRooms.observe(viewLifecycleOwner) { mChatRooms ->
                mAdapter?.setData(mChatRooms)
            }
        }

        binding?.run {
            ivAddGroup.setOnClickListener {

                val addGroupFragment = AddGroupFragment.newInstance().apply {
                    onDoneListener = { isSuccess, roomUID ->
                        if (isSuccess) {
                            roomUID?.let { mViewModel?.addNewRoomToList(roomUID) }
                        }
                    }
                }

                parentFragment?.addFragment(
                    R.id.fragmentContainerView,
                    addGroupFragment,
                    true,
                    AddGroupFragment::class.java.name,
                    enterAnim = R.anim.slide_bottom_in,
                    popExit = R.anim.slide_bottom_out
                )
            }
        }

        mAdapter?.onItemClickListener = { roomId ->

            if (mViewModel?.checkIsTogglePIN() == false) {
                openChatScreenFragment(roomId)
            } else {
                EnterPinDialog.newInstance().apply {
                    onYesClickListener = {
                        if(mViewModel?.checkPIN(it) == true){
                            openChatScreenFragment(roomId)
                        }else{
                            Toast.makeText(context, "Wrong PIN code!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.show(childFragmentManager, EnterPinDialog.TAG)
            }

        }

        (activity as MainActivity).run {
            chatListHandleMessage = {
                mViewModel?.updateLatestMessage(it)
            }
        }


        listenTokenExpired()
        handleRefreshing()
        handleSearchRoom()
    }

    private fun openChatScreenFragment(roomId: String?) {
        roomId?.let {
            parentFragment?.addFragment(
                R.id.fragmentContainerView,
                ChatScreenFragment.newInstance(it),
                true,
                ChatScreenFragment::class.java.name,
                enterAnim = R.anim.slide_bottom_in,
                popExit = R.anim.slide_bottom_out
            )
        }
    }

    private fun listenTokenExpired() {
        mViewModel?.isTokenExpired?.observe(viewLifecycleOwner) { isExpired ->
            if (isExpired) {
                Toast.makeText(context, getString(R.string.author_expired), Toast.LENGTH_SHORT)
                    .show()
                (activity as MainActivity).handleLogoutExpired()
            }
        }
    }

    private fun handleRefreshing() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            loadList()
        }
    }

    private fun handleSearchRoom() {
        binding?.run {
            edtSearch.addTextChangedListener { text ->

                mViewModel?.mChatRooms?.value?.let {
                    val filterList = it.filter { chatRoom ->
                        chatRoom.room?.name?.decodeBase64()?.lowercase()
                            ?.contains(text.toString().lowercase()) == true ||
                                chatRoom.participant?.user?.name?.decodeBase64()?.lowercase()
                                    ?.contains(text.toString().lowercase()) == true
                    }

                    mAdapter?.setData(filterList)
                }

            }
        }
    }

}