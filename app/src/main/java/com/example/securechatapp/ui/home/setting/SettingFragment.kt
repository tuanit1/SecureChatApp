package com.example.securechatapp.ui.home.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private var binding: FragmentSettingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding?.root
    }


}