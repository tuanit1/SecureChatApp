package com.example.securechatapp.ui.home.userlist

import android.os.Bundle
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
import com.example.securechatapp.databinding.FragmentUserListBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.ui.home.chatscreen.ChatScreenFragment
import com.example.securechatapp.utils.InjectorUtils

class UserListFragment : Fragment() {

    companion object {
        fun newInstance() = UserListFragment()
    }

    private var mViewModel: UserListViewModel? = null
    private var binding: FragmentUserListBinding? = null
    private var mAdapter: UserListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initListener() {
        mAdapter?.onItemClickListener = { userId ->
            mViewModel?.openPrivateChatScreen(
                otherUID = userId,
                callback = { isSuccess, roomID ->
                    if(isSuccess){
                        roomID?.let { openChatListFragment(it) }
                    }else{
                        Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    fun openChatListFragment(roomID: String){
        parentFragment?.addFragment(
            R.id.fragmentContainerView,
            ChatScreenFragment.newInstance(roomID),
            true,
            ChatScreenFragment::class.java.name
        )
    }

    private fun observeUserList() {
        mViewModel?.run {
            mUsers.observe(viewLifecycleOwner){ list ->
                mAdapter?.submitList(list)
            }
        }
    }

    private fun loadUserList() {
        mViewModel?.loadUserList(object : APICallback{
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

    private fun initView() {
        val factory = InjectorUtils.provideUserListViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]

        mAdapter = UserListAdapter()
        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context)
            rv.itemAnimator = DefaultItemAnimator()
        }

        observeUserList()
        loadUserList()

    }

}