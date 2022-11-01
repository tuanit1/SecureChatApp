package com.example.securechatapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.core.widget.doOnTextChanged
import com.example.securechatapp.R
import com.example.securechatapp.databinding.LayoutCustomFormButtonBinding
import com.google.android.material.textfield.TextInputLayout

class CustomFormButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr){
    private var binding: LayoutCustomFormButtonBinding? = null

    var showProgress: (Boolean) -> Unit = {}


    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        binding = LayoutCustomFormButtonBinding.inflate(LayoutInflater.from(context), this, true)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFormButton)
        try {
            binding?.apply {
                tvTitle.text = styleAttrs.getString(R.styleable.CustomFormButton_title)

                showProgress = {
                    if(it){
                        progressBar.visibility = VISIBLE
                        tvTitle.visibility = GONE
                    }else{
                        progressBar.visibility = GONE
                        tvTitle.visibility = VISIBLE
                    }
                }
            }
        } finally {
            styleAttrs.recycle()
        }
    }
}