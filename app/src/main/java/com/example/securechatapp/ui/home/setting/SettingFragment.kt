package com.example.securechatapp.ui.home.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentSettingBinding
import com.example.securechatapp.extension.replaceFragment
import com.example.securechatapp.ui.auth.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private var auth: FirebaseAuth? = null
    private var binding: FragmentSettingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initListener()
    }

    private fun initListener() {
        binding?.run {
            btnLogout.setOnClickListener {
                handleSignOut()
            }
        }
    }

    private fun handleSignOut(){
        auth?.signOut()

        parentFragment?.replaceFragment(
            R.id.fragmentContainerView,
            LoginFragment.newInstance(),
            addToBackStack = true,
            tag = LoginFragment::class.java.name
        )
    }


}