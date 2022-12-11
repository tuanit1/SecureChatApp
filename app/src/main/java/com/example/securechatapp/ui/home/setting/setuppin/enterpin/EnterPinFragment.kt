package com.example.securechatapp.ui.home.setting.setuppin.enterpin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.securechatapp.databinding.FragmentEnterPinBinding
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.ui.home.setting.setuppin.SetupPinFragment

class EnterPinFragment : Fragment() {

    private var binding: FragmentEnterPinBinding? = null
    private var mPinCode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterPinBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding?.run {
            editPin.onTextChange = {
                mPinCode = it
            }

            btnNext.setOnClickListener {
                if(mPinCode.length == 6){

                    hideKeyboardFrom(requireContext(), editPin)
                    Toast.makeText(context, "PIN saved", Toast.LENGTH_SHORT).show()

                    (parentFragment as SetupPinFragment).run {
                        savePin(mPinCode)
                        popFragment()
                    }

                    (activity as MainActivity).onTogglePINResult(true)
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
        const val TAG = "EnterPinFragment"
        @JvmStatic
        fun newInstance() = EnterPinFragment()
    }
}