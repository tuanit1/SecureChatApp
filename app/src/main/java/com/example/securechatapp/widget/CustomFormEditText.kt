package com.example.securechatapp.widget

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.securechatapp.R
import com.example.securechatapp.databinding.LayoutCustomFormEdtBinding
import com.google.android.material.textfield.TextInputLayout

class CustomFormEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr){
    var _binding: LayoutCustomFormEdtBinding? = null

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        _binding = LayoutCustomFormEdtBinding.inflate(LayoutInflater.from(context), this, true)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFormEditText)
        try {
            _binding?.apply {
                textInputLayout.hint = styleAttrs.getString(R.styleable.CustomFormEditText_hintText)

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