package com.example.securechatapp.ui.home.addgroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.FragmentAddGroupBinding
import com.example.securechatapp.utils.InjectorUtils

class AddGroupFragment : Fragment() {

    private var binding: FragmentAddGroupBinding? = null
    private var mViewModel: AddGroupViewModel? = null
    private var mAdapter: UserListAdapter? = null
    private var mUserList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGroupBinding.inflate(inflater)
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

            btnConfirm.setOnClickListener{
                handleConfirmClick()
            }
        }
    }

    private fun handleConfirmClick() {
        binding?.run {
            val groupName = edtGroupName.text

            if(groupName.isEmpty()){
                edtGroupName.error = "Please enter group name!"
            }else{
                mViewModel?.addNewGroup(groupName.toString())
            }
        }
    }

    private fun initView() {

        mAdapter = UserListAdapter(mUserList)
        mAdapter?.onCheckUser = { index, isCheck ->
            mViewModel?.checkUser(index, isCheck)
        }

        val factory = InjectorUtils.provideAddGroupViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[AddGroupViewModel::class.java]

        observerUserList()

        binding?.run {
            rvUser.adapter = mAdapter
            rvUser.itemAnimator = DefaultItemAnimator()
            rvUser.layoutManager = LinearLayoutManager(context)
        }

        loadUserList()
    }

    private fun loadUserList() {
        mViewModel?.run {
            if(!isLoaded){
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
        }
    }

    private fun observerUserList() {
        mViewModel?.run {
            mUsers.observe(viewLifecycleOwner){ list ->
                mUserList.clear()
                mUserList.addAll(list)
                mAdapter?.submitList(mUserList)
            }
        }
    }

    companion object {
        fun newInstance() = AddGroupFragment()
    }

}