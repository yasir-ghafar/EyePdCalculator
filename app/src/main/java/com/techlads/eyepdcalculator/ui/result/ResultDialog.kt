package com.techlads.eyepdcalculator.ui.result

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.databinding.DialogPdResultLayoutBinding
import com.techlads.eyepdcalculator.utils.Constants

class ResultDialog : DialogFragment() {

    companion object {
        const val TAG = "ResultDialog"

        fun newInstance(result: Float): ResultDialog {
            return ResultDialog().apply {
                arguments = bundleOf(Constants.PD_RESULT to result.toString())
                isCancelable = false
            }
        }
    }

    var onCloseClickListener: OnCloseClickListener? = null

    private lateinit var binding: DialogPdResultLayoutBinding

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPdResultLayoutBinding.inflate(LayoutInflater.from(context))
        bindResult()
        binding.btnClose.setOnClickListener {
            onCloseClickListener?.onClose(dialog)
        }
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
            .also { dialog ->
                dialog.window?.attributes?.gravity = Gravity.CENTER
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
    }

    private fun bindResult() {
        val result = arguments?.getString(Constants.PD_RESULT).orEmpty()
        binding.resultTv.text = getString(R.string.pd_result_message, result)
    }

    fun interface OnCloseClickListener {
        fun onClose(dialogFragment: Dialog?)
    }
}
