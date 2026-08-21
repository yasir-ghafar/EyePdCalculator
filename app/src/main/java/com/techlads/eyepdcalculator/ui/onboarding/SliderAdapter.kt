package com.techlads.eyepdcalculator.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.utils.Constants.CURRENT_SLIDE_TEXT

class SliderAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val guideLines: Array<String> by lazy {
        arrayOf(
            activity.getString(R.string.slide_one_info),
            activity.getString(R.string.slide_two_info),
            activity.getString(R.string.slide_three_info),
            activity.getString(R.string.slide_four_info),
        )
    }

    override fun getItemCount(): Int = guideLines.size

    override fun createFragment(position: Int): Fragment {
        return CurrentOnBoardingFragment().apply {
            arguments = Bundle().apply {
                putString(CURRENT_SLIDE_TEXT, guideLines[position])
            }
        }
    }
}
