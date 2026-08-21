package com.techlads.eyepdcalculator.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.techlads.eyepdcalculator.ui.result.ResultDialog

abstract class BaseFragment<B : ViewBinding> : Fragment() {

    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onPostInit()
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun onPostInit()

    protected fun showResultDialog(result: Float) {
        ResultDialog.newInstance(result).apply {
            onCloseClickListener = ResultDialog.OnCloseClickListener { dismiss() }
        }.show(childFragmentManager, ResultDialog.TAG)
    }
}
