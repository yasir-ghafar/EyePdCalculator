package com.techlads.eyepdcalculator.ui.home

import android.net.Uri

data class HomeUiState(
    val capturedImageUri: Uri? = null,
    val hasCapturedImage: Boolean = false
)
