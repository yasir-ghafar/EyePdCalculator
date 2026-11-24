package com.techlads.eyepdcalculator.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentOnBoardingSliderBinding
import com.techlads.eyepdcalculator.ui.theme.VernierTheme
import kotlinx.coroutines.launch

class OnBoardingSliderFragment : BaseFragment<FragmentOnBoardingSliderBinding>() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardingSliderBinding {
        return FragmentOnBoardingSliderBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.onboardingCompose.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.onboardingCompose.setContent {
            VernierTheme {
                OnboardingScreen(onFinished = viewModel::onFinished)
            }
        }
        observeEvents()
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
}
