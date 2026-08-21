package com.techlads.eyepdcalculator.ui.calculate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentCalculatePdBinding
import kotlinx.coroutines.launch

class CalculatePdFragment : BaseFragment<FragmentCalculatePdBinding>() {

    private val viewModel: CalculatePdViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalculatePdBinding {
        return FragmentCalculatePdBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.markerPupilOne.setTouchListener()
        binding.markerPupilTwo.setTouchListener()
        binding.markerCardOne.setTouchListener()
        binding.markerCardTwo.setTouchListener()

        binding.btGotoNext.setOnClickListener {
            viewModel.onNextClicked(
                pupilOneX = binding.markerPupilOne.x,
                pupilOneY = binding.markerPupilOne.y,
                pupilTwoX = binding.markerPupilTwo.x,
                pupilTwoY = binding.markerPupilTwo.y,
                cardOneX = binding.markerCardOne.x,
                cardOneY = binding.markerCardOne.y,
                cardTwoX = binding.markerCardTwo.x,
                cardTwoY = binding.markerCardTwo.y
            )
        }

        observeState()
        observeEvents()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.imageUri?.let { uri ->
                        binding.bgIv.setImageURI(uri)
                    }
                    binding.markerPupilOne.setVisibleOrInvisible(state.showPupilMarkers)
                    binding.markerPupilTwo.setVisibleOrInvisible(state.showPupilMarkers)
                    binding.markerCardOne.setVisibleOrInvisible(state.showCardMarkers)
                    binding.markerCardTwo.setVisibleOrInvisible(state.showCardMarkers)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is CalculatePdEvent.ShowResult -> showResultDialog(event.pdMm)
                        CalculatePdEvent.ShowInvalidStateMessage -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.pd_calculation_error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun View.setVisibleOrInvisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.setTouchListener() {
        setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = motionEvent.rawY - view.height / 2
                view.x = motionEvent.rawX - view.width / 2
            }
            true
        }
    }
}
