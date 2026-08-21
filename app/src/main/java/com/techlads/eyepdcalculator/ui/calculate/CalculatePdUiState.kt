package com.techlads.eyepdcalculator.ui.calculate

import android.net.Uri

enum class CalculateStep {
    MARK_PUPILS,
    MARK_CARD
}

data class CalculatePdUiState(
    val imageUri: Uri? = null,
    val step: CalculateStep = CalculateStep.MARK_PUPILS,
    val showPupilMarkers: Boolean = true,
    val showCardMarkers: Boolean = false
)
