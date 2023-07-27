package com.example.securechatapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.securechatapp.widget.LoadingProgressDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    protected abstract val viewModel: VM
    protected val binding get() = _binding
    private val circleProgress: LoadingProgressDialog by lazy { LoadingProgressDialog() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBase()

        initView()
        initListener()
    }

    private fun initBase() {
        lifecycleScope.launch {
            viewModel.progressState.collectLatest { state ->
                childFragmentManager.beginTransaction().run {
                    if (state.isShow) {
                        show(circleProgress)
                    } else {
                        hide(circleProgress)
                    }
                    commit()
                }
            }
        }
    }

    open fun initView() {}
    open fun initListener() {}
}