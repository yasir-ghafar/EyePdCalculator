package com.techlads.eyepdcalculator.ui.calculate

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
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
    private var renderedStep: CalculateStep? = null
    private var markersPlaced = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalculatePdBinding {
        return FragmentCalculatePdBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        markersPlaced = false
        renderedStep = null
        binding.markerPupilOne.setTouchListener()
        binding.markerPupilTwo.setTouchListener()
        binding.markerCardOne.setTouchListener()
        binding.markerCardTwo.setTouchListener()
        binding.btnReset.setOnClickListener {
            placeMarkers()
            binding.btnReset.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
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
        binding.root.doOnLayout {
            if (!markersPlaced) {
                placeMarkers()
                markersPlaced = true
            }
        }

        observeState()
        observeEvents()
    }

    private fun placeMarkers() {
        val parent = binding.root
        position(binding.markerPupilOne, 0.30f, 0.52f, parent)
        position(binding.markerPupilTwo, 0.70f, 0.52f, parent)
        position(binding.markerCardOne, 0.36f, 0.38f, parent)
        position(binding.markerCardTwo, 0.64f, 0.38f, parent)
        refreshLink()
    }

    private fun position(marker: View, xFraction: Float, yFraction: Float, parent: View) {
        marker.x = parent.width * xFraction - marker.width / 2f
        marker.y = parent.height * yFraction - marker.height / 2f
    }

    private fun refreshLink() {
        val pupils = viewModel.uiState.value.showPupilMarkers
        val first = if (pupils) binding.markerPupilOne else binding.markerCardOne
        val second = if (pupils) binding.markerPupilTwo else binding.markerCardTwo
        val color = ContextCompat.getColor(
            requireContext(),
            if (pupils) R.color.mint else R.color.amber
        )
        binding.linkOverlay.setEndpoints(
            x1 = first.x + first.width / 2f,
            y1 = first.y + first.height / 2f,
            x2 = second.x + second.width / 2f,
            y2 = second.y + second.height / 2f,
            color = color
        )
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.imageUri?.let { uri ->
                        binding.bgIv.setImageURI(uri)
                    }
                    val stepChanged = renderedStep != state.step
                    renderedStep = state.step
                    showMarker(binding.markerPupilOne, state.showPupilMarkers, stepChanged)
                    showMarker(binding.markerPupilTwo, state.showPupilMarkers, stepChanged)
                    showMarker(binding.markerCardOne, state.showCardMarkers, stepChanged)
                    showMarker(binding.markerCardTwo, state.showCardMarkers, stepChanged)
                    if (state.step == CalculateStep.MARK_PUPILS) {
                        binding.stepLabel.setText(R.string.mark_step_pupils)
                        binding.stepTitle.setText(R.string.mark_title_pupils)
                        binding.stepBody.setText(R.string.mark_body_pupils)
                        binding.btGotoNext.setText(R.string.mark_action_pupils)
                        binding.stepLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.mint))
                    } else {
                        binding.stepLabel.setText(R.string.mark_step_card)
                        binding.stepTitle.setText(R.string.mark_title_card)
                        binding.stepBody.setText(R.string.mark_body_card)
                        binding.btGotoNext.setText(R.string.mark_action_card)
                        binding.stepLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber))
                    }
                    binding.linkOverlay.post { refreshLink() }
                }
            }
        }
    }

    private fun showMarker(marker: View, visible: Boolean, animate: Boolean) {
        if (!animate) {
            marker.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            marker.alpha = 1f
            marker.scaleX = 1f
            marker.scaleY = 1f
            return
        }
        marker.animate().cancel()
        if (visible) {
            marker.visibility = View.VISIBLE
            marker.alpha = 0f
            marker.scaleX = 0.82f
            marker.scaleY = 0.82f
            marker.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(220).start()
        } else {
            marker.animate().alpha(0f).scaleX(0.86f).scaleY(0.86f).setDuration(140).withEndAction {
                marker.visibility = View.INVISIBLE
                marker.alpha = 1f
                marker.scaleX = 1f
                marker.scaleY = 1f
            }.start()
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

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.setTouchListener() {
        setOnTouchListener { view, event ->
            val parent = view.parent as View
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(1.16f).scaleY(1.16f).setDuration(90).start()
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val location = IntArray(2)
                    parent.getLocationOnScreen(location)
                    val targetX = event.rawX - location[0] - view.width / 2f
                    val targetY = event.rawY - location[1] - view.height / 2f
                    val maxX = (parent.width - view.width).toFloat().coerceAtLeast(0f)
                    val maxY = (parent.height - view.height).toFloat().coerceAtLeast(0f)
                    view.x = targetX.coerceIn(0f, maxX)
                    view.y = targetY.coerceIn(0f, maxY)
                    refreshLink()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    true
                }
                else -> false
            }
        }
    }
}
