package com.example.securechatapp.ui.home.chatscreen.chatsetting.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.DialogUserPrivilegeBinding
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.extension.getWidthScreen
import com.example.securechatapp.extension.jsonToObject
import com.squareup.picasso.Picasso

class UserPrivilegeDialog : DialogFragment() {

    companion object {
        const val TAG = "UserPrivilegeDialog"
        const val WIDTH_RADIO = 0.8f
        const val IS_ADMIN = "is_admin"
        const val PARTICIPANT = "participant"

        fun newInstance(isAdmin: Boolean, participant: String) = UserPrivilegeDialog().apply {
            arguments = Bundle().apply {
                putBoolean(IS_ADMIN, isAdmin)
                putString(PARTICIPANT, participant)
            }
        }

    }

    private var binding: DialogUserPrivilegeBinding? = null
    private var isAdmin: Boolean? = null
    private var mParticipant: Participant? = null
    var onUpdateClickListener: (Participant, APICallback) -> Unit = {_,_ ->}
    var onResponseMessage: (String) -> Unit = {}

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
        isAdmin = arguments?.getBoolean(IS_ADMIN)
        arguments?.getString(PARTICIPANT)?.let { json ->
            mParticipant = jsonToObject<Participant>(json)
        }

        binding?.run {
            if (isAdmin == false) {
                btnKick.visibility = View.GONE
                btnUpdate.visibility = View.GONE
                cbSendFile.isEnabled = false
                cbViewFile.isEnabled = false
                cbSendMessage.isEnabled = false
            }

            mParticipant?.run {
                cbViewFile.isChecked = allowViewFile
                cbSendMessage.isChecked = allowSendMSG
                cbSendFile.isChecked = allowSendFile

                tvName.text = user.name.decodeBase64()

                val imageUrl = user.image.decodeBase64()

                if (imageUrl.isNotEmpty()) {
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_user_placeholder2)
                        .into(ivThumb)
                } else {
                    Picasso.get()
                        .load(R.drawable.ic_user_placeholder2)
                        .into(ivThumb)
                }

                btnUpdate.setOnClickListener {

                    mParticipant?.apply {
                        allowSendFile = cbSendFile.isChecked
                        allowSendMSG = cbSendMessage.isChecked
                        allowViewFile = cbViewFile.isChecked

                        onUpdateClickListener(this, object : APICallback{
                            override fun onStart() {
                                clButton.visibility = View.GONE
                                pbButton.visibility = View.VISIBLE
                            }

                            override fun onSuccess(data: Any?) {
                                clButton.visibility = View.VISIBLE
                                pbButton.visibility = View.GONE

                                onResponseMessage("Update privilege successfully!")

                                dismiss()
                            }

                            override fun onError(t: Throwable?) {
                                clButton.visibility = View.GONE
                                pbButton.visibility = View.VISIBLE

                                onResponseMessage("Fail when update privilege, try again!")
                            }

                        })
                    }
                }
            }

        }

    }
}