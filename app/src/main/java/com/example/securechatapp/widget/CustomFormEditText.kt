package com.example.securechatapp.widget

import android.content.Context
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import com.example.securechatapp.R
import com.example.securechatapp.databinding.LayoutCustomFormEdtBinding
import com.google.android.material.textfield.TextInputLayout

class CustomFormEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr){
    private var binding: LayoutCustomFormEdtBinding? = null

    fun getText(): String = binding?.edtForm?.text.toString()
    var onTextChange: (text: String) -> Unit = {}

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        binding = LayoutCustomFormEdtBinding.inflate(LayoutInflater.from(context), this, true)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFormEditText)
        try {
            binding?.apply {
                textInputLayout.hint = styleAttrs.getString(R.styleable.CustomFormEditText_hintText)

                edtForm.doOnTextChanged { text, _, _, _ ->
                    onTextChange(text.toString())
                }

                ivIcon.setImageDrawable(styleAttrs.getDrawable(R.styleable.CustomFormEditText_image))

                if(styleAttrs.getBoolean(R.styleable.CustomFormEditText_isPassword, false)){
                    textInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                    edtForm.transformationMethod = PasswordTransformationMethod.getInstance()
                }else{
                    textInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    edtForm.transformationMethod = null
                }

            }
        } finally {
            styleAttrs.recycle()
        }
    }
}