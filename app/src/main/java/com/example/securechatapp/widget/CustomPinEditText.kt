package com.example.securechatapp.widget

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.widget.doOnTextChanged
import com.example.securechatapp.R
import com.example.securechatapp.databinding.LayoutCustomPinEdtBinding


class CustomPinEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr){
    private var binding: LayoutCustomPinEdtBinding? = null

    var onTextChange: (text: String) -> Unit = {}

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        binding = LayoutCustomPinEdtBinding.inflate(LayoutInflater.from(context), this, true)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomPinEditText)

        try {
            binding?.apply {

                edtPin.filters = arrayOf(InputFilter.LengthFilter(styleAttrs.getInt(R.styleable.CustomPinEditText_android_maxLength, 6)))

                edtPin.doOnTextChanged { text, _, _, _ ->
                    onTextChange(text.toString())
                }

            }
        } finally {
            styleAttrs.recycle()
        }
    }

    fun setError(error: String){
        binding?.edtPin?.error = error
    }

}