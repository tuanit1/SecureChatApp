package com.example.securechatapp.widget

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.securechatapp.databinding.DialogMessageBinding
import com.example.securechatapp.extension.getWidthScreen

class MessageDialog : DialogFragment() {

    companion object {
        const val TAG = "MessageDialog"
        const val WIDTH_RADIO = 0.8f
        const val TITLE = "title"

        fun newInstance(title: String) = MessageDialog().apply {
            arguments = Bundle().apply {
                putString(TITLE, title)
            }
        }

    }

    private var binding: DialogMessageBinding? = null
    var onYesClickListener: () -> Unit = {}
    var onNoClickListener: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMessageBinding.inflate(inflater)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogDescription = Dialog(requireContext())
        dialogDescription.window?.run {
            isCancelable = false
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            requestFeature(Window.FEATURE_NO_TITLE)
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
            btnYes.setOnClickListener {
                onYesClickListener.invoke()
                dismiss()
            }

            ivCancel.setOnClickListener {
                onNoClickListener.invoke()
                dismiss()
            }
        }
    }

    private fun initView() {
        val title = arguments?.getString(TITLE)

        binding?.run {
            tvTitle.text = title
        }

    }
}