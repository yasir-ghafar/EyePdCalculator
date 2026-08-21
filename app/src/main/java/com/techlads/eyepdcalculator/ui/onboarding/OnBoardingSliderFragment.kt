package com.techlads.eyepdcalculator.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentOnBoardingSliderBinding
import kotlinx.coroutines.launch

class OnBoardingSliderFragment : BaseFragment<FragmentOnBoardingSliderBinding>() {

    private val viewModel: OnBoardingViewModel by viewModels()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onPageSelected(position)
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardingSliderBinding {
        return FragmentOnBoardingSliderBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.viewPager.adapter = SliderAdapter(requireActivity())
        binding.indicator.setViewPager(binding.viewPager)
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.btnNext.setOnClickListener { viewModel.onNextClicked() }

        observeState()
        observeEvents()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.btnNext.visibility =
                        if (state.isNextVisible) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        OnBoardingEvent.NavigateToHome -> {
                            findNavController().navigate(
                                R.id.action_onBoardingFragment_to_homeFragment
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroyView()
    }
}
