package com.techlads.eyepdcalculator.ui.onboarding

data class OnBoardingUiState(
    val currentPage: Int = 0,
    val isNextVisible: Boolean = false,
    val pageCount: Int = 4
)
