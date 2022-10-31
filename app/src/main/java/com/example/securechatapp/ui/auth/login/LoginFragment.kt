package com.example.securechatapp.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentLoginBinding
import com.example.securechatapp.extension.replaceFragment
import com.example.securechatapp.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var auth: FirebaseAuth
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        auth = Firebase.auth

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()

    }

    private fun initListener() {
        binding?.run {
        }
    }

    private fun handleLoginClick() {
        binding?.run {
            val email: String = edtEmail.getText()
            val password: String = edtPass.getText()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(context, "Some field is missing, please check again!", Toast.LENGTH_SHORT).show();
            }else{
                btnLogin.showProgress(true)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        btnLogin.showProgress(false)
                        if(it.isSuccessful){
                            Toast.makeText(context, "login successfully!", Toast.LENGTH_SHORT).show()
                            openHomeFragment()
                        }else{
                            it.exception?.let { e ->
                                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener{
                        btnLogin.showProgress(false)
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun openHomeFragment() {

        replaceFragment(
            getContainerId(),
            HomeFragment.newInstance(),
            addToBackStack = true,
            tag = HomeFragment::class.java.name
        )
    }

    private fun getContainerId() = R.id.fragmentContainerView



}