package com.example.securechatapp.ui.home.chatsetting.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.securechatapp.databinding.DialogUserPrivilegeBinding
import com.example.securechatapp.extension.getWidthScreen

class UserPrivilegeDialog: DialogFragment() {

    companion object{
        const val TAG = "UserPrivilegeDialog"
        const val WIDTH_RADIO = 0.8f

    }

    private var binding: DialogUserPrivilegeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogUserPrivilegeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogDescription = Dialog(requireContext())
        dialogDescription.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            requestFeature(Window.FEATURE_NO_TITLE)
            isCancelable = true
        }
        return dialogDescription
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    override fun onStart() {
        super.onStart()
        val screenWidth = requireContext().getWidthScreen()
        dialog?.window?.run {
            attributes = attributes.apply {
                width = (screenWidth * WIDTH_RADIO).toInt()
            }
        }
    }

    private fun initListener() {
        binding?.run {
            ivCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initView() {
    }
}