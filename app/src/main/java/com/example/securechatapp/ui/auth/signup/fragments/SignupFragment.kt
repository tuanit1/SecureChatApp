package com.example.securechatapp.ui.auth.signup.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.securechatapp.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {


    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var auth: FirebaseAuth

    private var binding: FragmentSignupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)


        initView()
        initListener()



        return binding?.root
    }

    private fun initListener() {
        binding?.run {
            btnSignup.setOnClickListener{
                handleSignup()
            }
        }
    }

    private fun handleSignup() {
        binding?.run {
            val email: String = edtEmail.getText()
            val password: String = edtEmail.getText()
            val age: String = edtAge.getText()
            val phone: String = edtPhone.getText()
            val name: String = edtName.getText()

            if(email.isEmpty() || password.isEmpty() || phone.isEmpty() || name.isEmpty()){
                Toast.makeText(context, "Some field is missing, please check again!", Toast.LENGTH_SHORT).show();
            }else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        Log.e("tuan", it.result.toString())
                    }.addOnFailureListener{
                        Log.e("tuan", it.message.toString())
                    }
            }
        }
    }

    private fun initView() {
        auth = Firebase.auth
    }

}