package com.example.securechatapp.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.cardview.widget.CardView
import com.example.securechatapp.R
import com.example.securechatapp.databinding.LayoutCustomFormEdtBinding

class CustomFormEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr){
    private var binding: LayoutCustomFormEdtBinding? = null

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        binding = LayoutCustomFormEdtBinding.inflate(LayoutInflater.from(context), this, true)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFormEditText)
        try {
            binding?.apply {
//                edtForm.hint = styleAttrs.getString(R.styleable.CustomFormEditText_hintText)
                ivIcon.setImageDrawable(styleAttrs.getDrawable(R.styleable.CustomFormEditText_image))

                textInputLayout.editText?.hint = "AAA"

                if(styleAttrs.getBoolean(R.styleable.CustomFormEditText_isPassword, false)){
                    edtForm.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                    textInputLayout.isPasswordVisibilityToggleEnabled = true
                }else{
                    edtForm.inputType = EditorInfo.TYPE_CLASS_TEXT
                    textInputLayout.isPasswordVisibilityToggleEnabled = false
                }
            }
        } finally {
            styleAttrs.recycle()
        }
    }
}