package com.example.securechatapp.widget

import com.example.securechatapp.base.BaseDialogFragment
import com.example.securechatapp.databinding.DialogLoadingProgressBinding
import com.example.securechatapp.extension.getHeightScreen
import com.example.securechatapp.extension.getWidthScreen

class LoadingProgressDialog :
    BaseDialogFragment<DialogLoadingProgressBinding>(DialogLoadingProgressBinding::inflate) {
    override fun onStart() {
        super.onStart()
        context?.run {
            dialog?.window?.run {
                attributes = attributes.apply {
                    width = getWidthScreen()
                    height = getHeightScreen()
                }
                setDimAmount(0f)
            }
        }
    }
}