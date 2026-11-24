package com.techlads.eyepdcalculator.ui.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: SplashViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.logoBadge.alpha = 0f
        binding.logoBadge.scaleX = 0.84f
        binding.logoBadge.scaleY = 0.84f
        binding.brandName.alpha = 0f
        binding.brandTagline.alpha = 0f
        binding.logoBadge.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(700).start()
        binding.brandName.animate().alpha(1f).setStartDelay(180).setDuration(420).start()
        binding.brandTagline.animate().alpha(1f).setStartDelay(320).setDuration(420).start()
        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        SplashEvent.NavigateToOnboarding -> {
                            findNavController().navigate(
                                R.id.action_splashFragment_to_onBoardingFragment
                            )
                        }
                    }
                }
            }
        }
    }
}
