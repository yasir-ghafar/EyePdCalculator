package com.techlads.eyepdcalculator.ui.result

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.Locale
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
                arguments = bundleOf(
                    Constants.PD_RESULT to String.format(Locale.US, "%.1f", result)
                )
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

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.88f).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun bindResult() {
        val result = arguments?.getString(Constants.PD_RESULT).orEmpty()
        binding.resultValue.text = result
        binding.resultTv.text = getString(R.string.result_caption)
    }

    fun interface OnCloseClickListener {
        fun onClose(dialogFragment: Dialog?)
    }
}
