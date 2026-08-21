package com.techlads.eyepdcalculator.ui.onboarding

sealed interface OnBoardingEvent {
    data object NavigateToHome : OnBoardingEvent
}
