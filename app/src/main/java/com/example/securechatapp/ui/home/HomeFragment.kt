package com.example.securechatapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
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
    private var mAdapter: ViewPagerAdapter? = null
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

        mAdapter = ViewPagerAdapter(this)

        binding?.run {
            viewPager.adapter = mAdapter
            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    when(position){
                        0 -> bottomNav.menu.findItem(R.id.itemNavMessage).isChecked = true
                        1 -> bottomNav.menu.findItem(R.id.itemNavSetting).isChecked = true
                    }
                }
            })
        }
    }

    private fun initListener() {
        binding?.run {
            bottomNav.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.itemNavMessage -> viewPager.currentItem = 0
                    R.id.itemNavSetting -> viewPager.currentItem = 1
                }

                return@setOnItemSelectedListener true
            }
        }
    }

}