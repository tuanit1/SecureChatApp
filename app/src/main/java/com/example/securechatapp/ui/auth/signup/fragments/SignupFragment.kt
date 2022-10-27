package com.example.securechatapp.ui.auth.signup.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.example.securechatapp.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {


    companion object {
        fun newInstance() = SignupFragment()
    }

    private var binding: FragmentSignupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)

        binding?.run {
            edtEmail.onTextChange = { text ->
                Log.e("AAAA", text)
            }
        }

        return binding?.root
    }

}