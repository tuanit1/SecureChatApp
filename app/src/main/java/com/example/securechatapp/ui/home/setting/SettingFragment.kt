package com.example.securechatapp.ui.home.setting

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
import com.example.securechatapp.data.model.User
import com.example.securechatapp.databinding.FragmentSettingBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.ui.home.setting.setuppin.SetupPinFragment
import com.example.securechatapp.utils.Constant
import com.example.securechatapp.utils.InjectorUtils
import com.example.securechatapp.widget.ConfirmDialog
import com.example.securechatapp.widget.EnterPinDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private var auth: FirebaseAuth? = null
    private var binding: FragmentSettingBinding? = null
    private var mViewModel: SettingViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() {
        auth = Firebase.auth

        val factory = InjectorUtils.provideSettingViewModelFactory(requireContext())
        mViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        loadUserImage()
        observerTogglePIN()
    }

    private fun observerTogglePIN() {
        mViewModel?.isTogglePIN?.observe(viewLifecycleOwner) {
            binding?.run {
                swTogglePIN.isChecked = it
                cvChangePIN.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }


    private fun initListener() {
        binding?.run {
            btnLogout.setOnClickListener {
                with(activity as MainActivity) {
                    handleLogout()
                }
            }

            swTogglePIN.setOnCheckedChangeListener { _, isCheck ->
                if(isCheck){

                    if(mViewModel?.isInitPin() == true){
                        mViewModel?.setTogglePINState(true)
                    }else{
                        swTogglePIN.isChecked = false
                        ConfirmDialog.newInstance(
                            title = getString(R.string.str_pin_not_set)
                        ).apply {
                            onYesClickListener = {
                                openSetUpPinFragment()
                            }
                        }.show(childFragmentManager, ConfirmDialog.TAG)
                    }


                }else{
                    EnterPinDialog.newInstance().apply {
                        onYesClickListener = {
                            if(mViewModel?.checkPin(it) == true){
                                mViewModel?.setTogglePINState(false)
                            }else{
                                Toast.makeText(requireContext(), "Wrong PIN code!", Toast.LENGTH_SHORT).show()
                                mViewModel?.updateToggleState()
                            }
                        }

                        onNoClickListener = {
                            mViewModel?.updateToggleState()
                        }
                    }.show(childFragmentManager, EnterPinDialog.TAG)
                }
            }

            cvChangePIN.setOnClickListener {
                openSetUpPinFragment()
            }

            (activity as MainActivity).onTogglePINResult = {
                swTogglePIN.isChecked = it
            }
        }
    }

    private fun openSetUpPinFragment(){
        parentFragment?.addFragment(
            R.id.fragmentContainerView,
            SetupPinFragment.newInstance(),
            true,
            SetupPinFragment.TAG,
            enterAnim = R.anim.slide_right_in,
            popExit = R.anim.slide_right_out
        )
    }

    private fun loadUserImage() {
        mViewModel?.getCurrentUser(Constant.mUID, object : APICallback {
            override fun onStart() = Unit

            override fun onSuccess(data: Any?) {
                if (data is User) {

                    val url = data.image.decodeBase64()

                    if (url.isNotEmpty()) {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    } else {
                        Picasso.get()
                            .load(R.drawable.ic_user_placeholder2)
                            .into(binding?.ivThumb)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("tuan", "load user image: ${t?.message}")
            }

        })
    }


}