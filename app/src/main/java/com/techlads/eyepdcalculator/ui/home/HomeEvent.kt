package com.techlads.eyepdcalculator.ui.home

import android.net.Uri

sealed interface HomeEvent {
    data object RequestCameraPermission : HomeEvent
    data object ShowPermissionRationale : HomeEvent
    data object PrepareCameraCapture : HomeEvent
    data class LaunchCamera(val uri: Uri) : HomeEvent
    data class NavigateToCalculate(val imageUri: Uri) : HomeEvent
    data object ShowNoImageMessage : HomeEvent
}
