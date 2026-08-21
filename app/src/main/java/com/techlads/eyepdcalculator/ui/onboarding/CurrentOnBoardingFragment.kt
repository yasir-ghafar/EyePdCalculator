package com.techlads.eyepdcalculator.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentCurrentOnBoardingBinding
import com.techlads.eyepdcalculator.utils.Constants.CURRENT_SLIDE_TEXT

class CurrentOnBoardingFragment : BaseFragment<FragmentCurrentOnBoardingBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCurrentOnBoardingBinding {
        return FragmentCurrentOnBoardingBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.guideLineText.text = arguments?.getString(CURRENT_SLIDE_TEXT).orEmpty()
    }
}
