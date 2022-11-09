package com.example.securechatapp

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.model.Playlist
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.databinding.ActivityMainBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.ui.auth.login.LoginFragment
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        initListener()
        initView()
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val count = supportFragmentManager.backStackEntryCount
                if(count > 1){
                    supportFragmentManager.popBackStack()
                }else{
                    finish()
                }
            }
        })
    }

    private fun initView() {

        auth?.run {
            if(currentUser == null){
                addFragment(
                    containerId = getContainerId(),
                    fragment = LoginFragment.newInstance(),
                    addToBackStack = true,
                    tag = getString(R.string.login)
                )
            }else{

                Constant.mUID = currentUser?.uid ?: ""

                addFragment(
                    containerId = getContainerId(),
                    fragment = HomeFragment.newInstance(),
                    addToBackStack = true,
                    tag = HomeFragment::class.java.name
                )
            }
        }


    }

    private fun getContainerId() = R.id.fragmentContainerView


}