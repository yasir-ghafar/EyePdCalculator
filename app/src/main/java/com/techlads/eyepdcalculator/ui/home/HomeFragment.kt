package com.techlads.eyepdcalculator.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.base.BaseFragment
import com.techlads.eyepdcalculator.databinding.FragmentHomeBinding
import com.techlads.eyepdcalculator.utils.ImageCaptureHelper
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    private val takeImageResult = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        viewModel.onImageCaptureResult(isSuccess)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onPostInit() {
        binding.btRetake.setOnClickListener {
            viewModel.onCaptureClicked(
                hasCameraPermission = isCameraPermissionGranted(),
                shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            )
        }
        binding.btOpenCamera.setOnClickListener {
            viewModel.onContinueClicked()
        }

        observeState()
        observeEvents()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.capturedImageUri?.let { uri ->
                        binding.bgIv.setImageURI(uri)
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
                            Toast.makeText(
                                requireContext(),
                                R.string.app_name_splash,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        HomeEvent.PrepareCameraCapture -> {
                            val uri = ImageCaptureHelper.createTempImageUri(requireContext())
                            viewModel.onTempImageUriReady(uri)
                        }
                        is HomeEvent.LaunchCamera -> {
                            takeImageResult.launch(event.uri)
                        }
                        is HomeEvent.NavigateToCalculate -> {
                            val action = HomeFragmentDirections
                                .actionHomeFragmentToCalculateFragment(event.imageUri)
                            findNavController().navigate(action)
                        }
                        HomeEvent.ShowNoImageMessage -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.click_picture,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
