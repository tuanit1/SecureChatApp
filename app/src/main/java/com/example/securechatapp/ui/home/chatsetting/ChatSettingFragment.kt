package com.example.securechatapp.ui.home.chatsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securechatapp.R
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.FragmentChatSettingBinding
import com.example.securechatapp.ui.home.chatscreen.ChatScreenViewModel
import com.example.securechatapp.utils.InjectorUtils
import kotlin.math.log


class ChatSettingFragment : Fragment() {

    private var binding: FragmentChatSettingBinding? = null
    private var mViewModel: ChatSettingViewModel? = null
    private var mAdapter: UserSettingAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance() = ChatSettingFragment()
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

        observerList()
    }

    private fun observerList() {
        mViewModel?.mPartyList?.observe(viewLifecycleOwner){ list ->
            if(list.isNotEmpty()){
                mAdapter?.submitList(list)
            }
        }
    }

    private fun initView() {

        mAdapter = UserSettingAdapter()

        binding?.run {
            rvUser.adapter = mAdapter
            rvUser.itemAnimator = DefaultItemAnimator()
            rvUser.layoutManager = LinearLayoutManager(context)
        }

        val factory = InjectorUtils.provideChatSettingViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[ChatSettingViewModel::class.java]
    }

}