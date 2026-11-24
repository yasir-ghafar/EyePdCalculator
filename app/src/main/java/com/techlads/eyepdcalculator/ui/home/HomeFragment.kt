package com.techlads.eyepdcalculator.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentHomeBinding
import com.techlads.eyepdcalculator.utils.ImageCaptureHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var captureInFlight = false
    private var hintIndex = 0
    private var cameraGranted = false

    private val hints by lazy {
        listOf(
            getString(R.string.capture_hint_eyes),
            getString(R.string.capture_hint_card),
            getString(R.string.capture_hint_distance),
            getString(R.string.capture_hint_light)
        )
    }

    private val rotateHint = object : Runnable {
        override fun run() {
            if (!isAdded || viewModel.uiState.value.hasCapturedImage || !cameraGranted) return
            hintIndex = (hintIndex + 1) % hints.size
            binding.coachText.animate().alpha(0f).setDuration(160).withEndAction {
                if (!isAdded) return@withEndAction
                binding.coachText.text = hints[hintIndex]
                binding.coachText.animate().alpha(1f).setDuration(220).start()
            }.start()
            binding.coachText.postDelayed(this, HINT_INTERVAL_MS)
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraGranted = isGranted
        viewModel.onPermissionResult(isGranted)
        if (isGranted) {
            startCamera()
            render(viewModel.uiState.value)
            scheduleHints()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        cameraGranted = isCameraPermissionGranted()
        binding.permissionButton.setOnClickListener { requestCapturePermission() }
        binding.shutterButton.setOnClickListener { onShutter() }
        binding.retakeButton.setOnClickListener {
            viewModel.onRetake()
            scheduleHints()
        }
        binding.usePhotoButton.setOnClickListener { viewModel.onContinueClicked() }

        if (cameraGranted) {
            startCamera()
            scheduleHints()
        }

        observeState()
        observeEvents()
    }

    private fun requestCapturePermission() {
        viewModel.onCaptureClicked(
            hasCameraPermission = isCameraPermissionGranted(),
            shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        )
    }

    private fun onShutter() {
        if (!cameraGranted) {
            requestCapturePermission()
            return
        }
        if (captureInFlight) return
        captureInFlight = true
        binding.shutterButton.animate().scaleX(0.9f).scaleY(0.9f).setDuration(80).withEndAction {
            binding.shutterButton.animate().scaleX(1f).scaleY(1f).setDuration(140).start()
        }.start()
        binding.flashOverlay.animate().alpha(0.85f).setDuration(60).withEndAction {
            binding.flashOverlay.animate().alpha(0f).setDuration(220).start()
            takePicture()
        }.start()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (!isAdded) return@addListener
            val provider = cameraProviderFuture.get()
            if (!isAdded) return@addListener
            cameraProvider = provider
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            val selectors = listOf(
                CameraSelector.DEFAULT_FRONT_CAMERA to R.string.capture_eyebrow_front,
                CameraSelector.DEFAULT_BACK_CAMERA to R.string.capture_eyebrow_back
            )
            val boundLabel = selectors.firstNotNullOfOrNull { (selector, label) ->
                val bound = runCatching {
                    if (!provider.hasCamera(selector)) return@runCatching false
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        viewLifecycleOwner,
                        selector,
                        preview,
                        imageCapture
                    )
                    true
                }.getOrDefault(false)
                if (bound) label else null
            }
            if (boundLabel == null) {
                toast(R.string.capture_no_camera)
            } else {
                binding.coachEyebrow.setText(boundLabel)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePicture() {
        val capture = imageCapture
        if (capture == null) {
            captureInFlight = false
            toast(R.string.capture_failed)
            return
        }
        val context = requireContext().applicationContext
        val file = ImageCaptureHelper.createTempImageFile(context)
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture.takePicture(
            options,
            cameraExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = ImageCaptureHelper.uriFor(context, file)
                    binding.root.post {
                        captureInFlight = false
                        if (!isAdded) return@post
                        viewModel.onImageCaptured(uri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    binding.root.post {
                        captureInFlight = false
                        if (!isAdded) return@post
                        toast(R.string.capture_failed)
                    }
                }
            }
        )
    }

    private fun render(state: HomeUiState) {
        val reviewing = state.hasCapturedImage && state.capturedImageUri != null
        binding.capturedImage.isVisible = reviewing
        if (reviewing) {
            binding.capturedImage.setImageURI(state.capturedImageUri)
            binding.coachText.animate().cancel()
            binding.coachText.alpha = 1f
            binding.coachText.setText(R.string.capture_review_hint)
        } else if (cameraGranted && binding.coachText.text == getString(R.string.capture_review_hint)) {
            binding.coachText.alpha = 1f
            binding.coachText.text = hints[hintIndex]
        }
        binding.faceGuide.isVisible = cameraGranted && !reviewing
        binding.shutterButton.isVisible = cameraGranted && !reviewing
        binding.shutterLabel.isVisible = cameraGranted && !reviewing
        binding.reviewActions.isVisible = reviewing
        binding.permissionPanel.isVisible = !cameraGranted
        binding.previewView.isVisible = cameraGranted
    }

    private fun scheduleHints() {
        binding.coachText.removeCallbacks(rotateHint)
        if (cameraGranted && !viewModel.uiState.value.hasCapturedImage) {
            binding.coachText.postDelayed(rotateHint, HINT_INTERVAL_MS)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                    if (!state.hasCapturedImage && cameraGranted) {
                        scheduleHints()
                    } else {
                        binding.coachText.removeCallbacks(rotateHint)
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        HomeEvent.RequestCameraPermission -> {
                            cameraPermission.launch(Manifest.permission.CAMERA)
                        }
                        HomeEvent.ShowPermissionRationale -> {
                            toast(R.string.capture_permission_rationale)
                        }
                        is HomeEvent.NavigateToCalculate -> {
                            val action = HomeFragmentDirections
                                .actionHomeFragmentToCalculateFragment(event.imageUri)
                            findNavController().navigate(action)
                        }
                        HomeEvent.ShowNoImageMessage -> toast(R.string.capture_need_photo)
                    }
                }
            }
        }
    }

    private fun toast(messageRes: Int) {
        Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
    }

    private fun cameraExecutor(): ExecutorService {
        val existing = cameraExecutor
        if (existing != null && !existing.isShutdown) return existing
        return Executors.newSingleThreadExecutor().also { cameraExecutor = it }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        binding.coachText.removeCallbacks(rotateHint)
        binding.coachText.animate().cancel()
        cameraProvider?.unbindAll()
        cameraProvider = null
        cameraExecutor?.shutdown()
        cameraExecutor = null
        imageCapture = null
        super.onDestroyView()
    }

    companion object {
        private const val HINT_INTERVAL_MS = 3200L
    }
}
