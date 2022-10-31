package com.example.securechatapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentHomeBinding
import com.example.securechatapp.extension.replaceFragment
import com.example.securechatapp.ui.auth.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var binding: FragmentHomeBinding? = null
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        auth = Firebase.auth
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()

    }

    private fun initView() {
    }

    private fun initListener() {
        binding?.run {
            bottomNav.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.itemNavMessage -> run {

                    }
                    R.id.itemNavSetting -> run {

                    }
                }

                return@setOnItemSelectedListener true
            }
        }
    }

    private fun addFragmentToHost(fragment: Fragment, tag: String){

        childFragmentManager.run {
            if(findFragmentByTag(tag) == null){
                commit {
                    replace(R.id.fragmentContainerHome, fragment, tag)
                    addToBackStack(tag)
                }
            }
        }
    }

    private fun handleSignOut(){
        auth?.signOut()
        replaceFragment(
            getContainerId(),
            LoginFragment.newInstance(),
            addToBackStack = true,
            tag = LoginFragment::class.java.name
        )
    }



    private fun getContainerId() = R.id.fragmentContainerView
}