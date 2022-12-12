package com.example.securechatapp.ui.home.setting.setuppin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.securechatapp.R
import com.example.securechatapp.databinding.FragmentSetupPinBinding
import com.example.securechatapp.extension.addChildFragment
import com.example.securechatapp.ui.home.setting.setuppin.checkpassword.CheckPasswordFragment
import com.example.securechatapp.ui.home.setting.setuppin.enterpin.EnterPinFragment
import com.example.securechatapp.utils.InjectorUtils

class SetupPinFragment : Fragment() {

    companion object {
        const val TAG = "SetupPinFragment"

        @JvmStatic
        fun newInstance() = SetupPinFragment()
    }

    private var binding: FragmentSetupPinBinding? = null
    private var mViewModel: SetupPinViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupPinBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()

    }

    private fun initListener() {
        binding?.run {
            ivBack.setOnClickListener {
                childFragmentManager.run {
                    if (backStackEntryCount > 1) {
                        popBackStack()
                    } else {
                        popFragment()
                    }
                }

            }
        }
    }

    private fun initView() {

        val factory = InjectorUtils.provideSetupPinViewModelFactory(requireContext())
        mViewModel = ViewModelProvider(this, factory)[SetupPinViewModel::class.java]

        addCheckPwFragment()
    }

    private fun addCheckPwFragment() {
        addChildFragment(
            getSetupPinContainer(),
            CheckPasswordFragment.newInstance(),
            true,
            CheckPasswordFragment.TAG,
            enterAnim = R.anim.slide_left_out,
            popExit = R.anim.slide_left_in
        )
    }

    fun addEnterPINFragment() {
        addChildFragment(
            getSetupPinContainer(),
            EnterPinFragment.newInstance(),
            true,
            EnterPinFragment.TAG,
            enterAnim = R.anim.slide_right_in,
            popExit = R.anim.slide_right_out,
        )
    }

    fun savePin(pin: String) {
        mViewModel?.savePin(pin)
    }

    fun popFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun getSetupPinContainer() = R.id.setupPinContainer


}