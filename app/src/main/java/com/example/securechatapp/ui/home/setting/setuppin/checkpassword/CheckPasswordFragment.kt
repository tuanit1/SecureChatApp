package com.example.securechatapp.ui.home.setting.setuppin.checkpassword

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentCheckPasswordBinding
import com.example.securechatapp.ui.home.setting.setuppin.SetupPinFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class CheckPasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var binding: FragmentCheckPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckPasswordBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initView()
        initListener()
    }

    private fun initListener() {
        binding?.run {
            btnNext.setOnClickListener {
                handleNextClick()
            }
        }
    }

    private fun initView() {
        binding?.run {

            val email = auth.currentUser?.email

            tvEmail.text = getString(R.string.str_enter_pw_as_email, email)

            edtPass.setEditTextFocus(true)
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun handleNextClick() {
        binding?.run {
            val email: String = auth.currentUser?.email ?: ""
            val password: String = edtPass.getText()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(context, "Some field is missing, please check again!", Toast.LENGTH_SHORT).show()
            }else{
                btnNext.showProgress(true)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if(it.isSuccessful){

                            edtPass.setEditTextFocus(false)
                            hideKeyboardFrom(requireContext(), edtPass)

                            (parentFragment as SetupPinFragment).addEnterPINFragment()
                        }else{
                            it.exception?.let { e ->
                                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                        btnNext.showProgress(false)
                    }.addOnFailureListener{
                        btnNext.showProgress(false)
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        const val TAG = "CheckPasswordFragment"
        @JvmStatic
        fun newInstance() = CheckPasswordFragment()
    }
}