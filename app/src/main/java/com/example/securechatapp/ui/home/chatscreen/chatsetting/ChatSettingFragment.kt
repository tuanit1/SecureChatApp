package com.example.securechatapp.ui.home.chatscreen.chatsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.FragmentChatSettingBinding
import com.example.securechatapp.extension.toJsonString
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.ui.home.chatscreen.ChatScreenFragment
import com.example.securechatapp.ui.home.chatscreen.chatsetting.dialog.UserPrivilegeDialog
import com.example.securechatapp.utils.InjectorUtils


class ChatSettingFragment : Fragment() {

    private var binding: FragmentChatSettingBinding? = null
    private var mViewModel: ChatSettingViewModel? = null
    private var mAdapter: UserSettingAdapter? = null
    private var mRoomID: String? = null

    companion object {
        private const val ROOM_ID = "room_id"

        @JvmStatic
        fun newInstance(roomID: String?) = ChatSettingFragment().apply {
            arguments = Bundle().apply {
                putString(ROOM_ID, roomID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initListener() {
        binding?.run {
            ivCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        mAdapter?.onItemClick = { item ->
            mViewModel?.mParticipant?.value?.let { participant ->
                UserPrivilegeDialog.newInstance(
                    isAdmin = participant.isAdmin,
                    participant = item.toJsonString()
                ).apply {
                    mViewModel?.run {
                        onUpdateClickListener = { participant, callback ->
                            updateParticipant(participant, callback)
                        }
                    }

                    onResponseMessage = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }.show(childFragmentManager, UserPrivilegeDialog.TAG)
            }
        }

        listenParticipantUpdate()
        listenTokenExpired()
        observerParticipant()
        observerList()
    }

    private fun listenParticipantUpdate() {
        (activity as MainActivity).run {
            chatSettingHandleParticipant = {
                when(it.action){
                    Participant.ADD_ACTION -> {

                    }

                    Participant.UPDATE_ACTION -> {
                        mViewModel?.handleParticipantUpdateSocket(it)
                    }
                }
            }
        }
    }

    private fun observerParticipant() {
        binding?.run {
            mViewModel?.mParticipant?.observe(viewLifecycleOwner) { participant ->
                if (participant.isAdmin) {
                    ivOut.visibility = View.GONE
                    ivPlus.visibility = View.VISIBLE
                    tvConfirm.text = requireContext().resources.getString(R.string.add_member)
                } else {
                    ivOut.visibility = View.VISIBLE
                    ivPlus.visibility = View.GONE
                    tvConfirm.text = getString(R.string.exit_room)
                }
            }
        }

    }

    private fun observerList() {
        mViewModel?.mPartyList?.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                mAdapter?.submitList(list)
            }
        }
    }

    private fun initView() {

        mRoomID = arguments?.getString(ChatScreenFragment.ROOM_ID)

        mAdapter = UserSettingAdapter()

        binding?.run {
            rvUser.adapter = mAdapter
            rvUser.itemAnimator = DefaultItemAnimator()
            rvUser.layoutManager = LinearLayoutManager(context)
        }

        val factory = InjectorUtils.provideChatSettingViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[ChatSettingViewModel::class.java]

        loadPartyList()
    }

    private fun loadPartyList() {
        binding?.run {
            mRoomID?.let {
                mViewModel?.loadPartyList(it, object : APICallback {
                    override fun onStart() {
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onSuccess(data: Any?) {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(t: Throwable?) {
                        progressBar.visibility = View.GONE
                    }
                })
            }
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

}