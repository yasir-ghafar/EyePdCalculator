package com.techlads.eyepdcalculator.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var pendingCaptureUri: Uri? = null

    fun onCaptureClicked(hasCameraPermission: Boolean, shouldShowRationale: Boolean) {
        viewModelScope.launch {
            when {
                hasCameraPermission -> _events.emit(HomeEvent.PrepareCameraCapture)
                shouldShowRationale -> _events.emit(HomeEvent.ShowPermissionRationale)
                else -> _events.emit(HomeEvent.RequestCameraPermission)
            }
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            if (isGranted) {
                _events.emit(HomeEvent.PrepareCameraCapture)
            }
        }
    }

    fun onTempImageUriReady(uri: Uri) {
        pendingCaptureUri = uri
        viewModelScope.launch {
            _events.emit(HomeEvent.LaunchCamera(uri))
        }
    }

    fun onImageCaptureResult(isSuccess: Boolean) {
        val uri = pendingCaptureUri ?: return
        if (isSuccess) {
            _uiState.update {
                it.copy(capturedImageUri = uri, hasCapturedImage = true)
            }
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            val uri = _uiState.value.capturedImageUri
            if (uri != null) {
                _events.emit(HomeEvent.NavigateToCalculate(uri))
            } else {
                _events.emit(HomeEvent.ShowNoImageMessage)
            }
        }
    }
}
