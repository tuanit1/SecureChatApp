package com.example.securechatapp.ui.home.userlist

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
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.FragmentUserListBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.ui.home.chatscreen.ChatScreenFragment
import com.example.securechatapp.utils.Constant
import com.example.securechatapp.utils.InjectorUtils
import com.example.securechatapp.widget.EnterPinDialog
import com.squareup.picasso.Picasso

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

            binding?.progressBar?.visibility = View.VISIBLE

            mViewModel?.openPrivateChatScreen(
                otherUID = userId,
                callback = { isSuccess, roomID ->
                    if(isSuccess){
                        roomID?.let {
                            EnterPinDialog.newInstance().apply {
                                onYesClickListener = {
                                    if(mViewModel?.checkPIN(it) == true){
                                        openChatListFragment(roomID)
                                    }else{
                                        Toast.makeText(context, "Wrong PIN code!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.show(childFragmentManager, EnterPinDialog.TAG)


                        }
                    }else{
                        Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_SHORT).show()
                    }

                    binding?.progressBar?.visibility = View.GONE
                }
            )
        }

        listenTokenExpired()
        handleSearchRoom()
        handleRefreshing()
    }

    private fun listenTokenExpired() {
        mViewModel?.isTokenExpired?.observe(viewLifecycleOwner){ isExpired ->
            if(isExpired){
                Toast.makeText(context, getString(R.string.author_expired), Toast.LENGTH_SHORT).show()
                (activity as MainActivity).handleLogoutExpired()
            }
        }
    }

    private fun handleRefreshing() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            loadUserList()
        }
    }

    private fun openChatListFragment(roomID: String){
        parentFragment?.addFragment(
            R.id.fragmentContainerView,
            ChatScreenFragment.newInstance(roomID),
            true,
            ChatScreenFragment::class.java.name,
            enterAnim = R.anim.slide_left_in,
            popExit = R.anim.slide_left_out
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

    private fun initView() {
        val factory = InjectorUtils.provideUserListViewModelFactory(requireContext())
        mViewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]

        mAdapter = UserListAdapter()
        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context)
            rv.itemAnimator = DefaultItemAnimator()
        }

        observeUserList()
        loadUserList()
        loadUserImage()

    }

    private fun handleSearchRoom() {
        binding?.run {
            edtSearch.addTextChangedListener { text ->

                mViewModel?.mUsers?.value?.let {
                    val filterList = it.filter {  user ->
                        user.name.decodeBase64().lowercase().contains(text.toString().lowercase())
                    }

                    mAdapter?.submitList(filterList)
                }

            }
        }
    }

    private fun loadUserImage() {
        mViewModel?.getCurrentUser(Constant.mUID, object : APICallback{
            override fun onStart() = Unit

            override fun onSuccess(data: Any?) {
                if(data is User){

                    val url = data.image.decodeBase64()

                    if(url.isNotEmpty()){
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    }else{
                        Picasso.get()
                            .load(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("tuan", "load user image: ${t?.message}")
            }

        })
    }


}