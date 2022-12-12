package com.example.securechatapp.widget

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.securechatapp.databinding.DialogConfirmBinding
import com.example.securechatapp.databinding.DialogPinBinding
import com.example.securechatapp.extension.getWidthScreen

class EnterPinDialog : DialogFragment() {

    companion object {
        const val TAG = "EnterPinDialog"
        const val WIDTH_RADIO = 0.8f

        fun newInstance() = EnterPinDialog()

    }

    private var binding: DialogPinBinding? = null
    var onYesClickListener: (String) -> Unit = {}
    var onNoClickListener: () -> Unit = {}
    var mPin: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogPinBinding.inflate(inflater)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogDescription = Dialog(requireContext())
        dialogDescription.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            requestFeature(Window.FEATURE_NO_TITLE)
            isCancelable = false
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
                onNoClickListener()
                dismiss()
            }

            editPin.onTextChange = {
                mPin = it
            }

            btnYes.setOnClickListener {

                if(mPin.length == 6){
                    onYesClickListener(mPin)
                    dismiss()
                }else{
                    editPin.setError("Must be 6 digits!")
                }
            }

        }
    }

    private fun initView() {
    }
}