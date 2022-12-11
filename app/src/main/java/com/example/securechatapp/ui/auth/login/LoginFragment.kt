package com.example.securechatapp.ui.auth.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.databinding.FragmentLoginBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.replaceFragment
import com.example.securechatapp.ui.auth.signup.SignupFragment
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.utils.Constant
import com.example.securechatapp.utils.InjectorUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var auth: FirebaseAuth
    private var binding: FragmentLoginBinding? = null
    private var mViewModel: LoginViewModel? = null

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
        initView()
        initListener()
    }

    private fun initView() {
        val factory = InjectorUtils.provideLoginViewModelFactory(requireContext())
        mViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun initListener() {
        binding?.run {
            btnLogin.setOnClickListener{
                handleLoginClick()
            }

            tvSignup.setOnClickListener {
                addFragment(
                    getContainerId(),
                    SignupFragment.newInstance(),
                    true,
                    SignupFragment::class.java.name
                )
            }
        }
    }

    private fun handleLoginClick() {
        binding?.run {
            val email: String = edtEmail.getText()
            val password: String = edtPass.getText()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(context, "Some field is missing, please check again!", Toast.LENGTH_SHORT).show()
            }else{
                btnLogin.showProgress(true)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            Constant.mUID = it.result?.user?.uid ?: ""
                            Constant.mIsExpiredDialogShowed = false

                            if(Constant.mUID.isNotEmpty()){
                                mViewModel?.getAuthToken(object: APICallback{
                                    override fun onStart() = Unit

                                    override fun onSuccess(data: Any?) {
                                        btnLogin.showProgress(false)
                                        Toast.makeText(context, "Welcome ${it.result.user?.email}", Toast.LENGTH_SHORT).show()
                                        openHomeFragment()
                                    }

                                    override fun onError(t: Throwable?) {
                                        btnLogin.showProgress(false)
                                        Toast.makeText(context, "Fail when request authorization!", Toast.LENGTH_SHORT).show()
                                        Log.e("tuan", t?.message.toString())
                                    }

                                })
                            }


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