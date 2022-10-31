package com.example.securechatapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentHomeBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.replaceFragment
import com.example.securechatapp.ui.auth.login.fragments.LoginFragment
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