package com.example.securechatapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentHomeBinding
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
                        0 -> bottomNav.menu.findItem(R.id.itemNavRoom).isChecked = true
                        1 -> bottomNav.menu.findItem(R.id.itemNavUsers).isChecked = true
                        2 -> bottomNav.menu.findItem(R.id.itemNavSetting).isChecked = true
                    }
                }
            })
        }
    }

    private fun initListener() {
        binding?.run {
            bottomNav.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.itemNavRoom -> viewPager.currentItem = 0
                    R.id.itemNavUsers -> viewPager.currentItem = 1
                    R.id.itemNavSetting -> viewPager.currentItem = 2
                }

                return@setOnItemSelectedListener true
            }
        }
    }

}