package com.techlads.eyepdcalculator.ui.splash

sealed interface SplashEvent {
    data object NavigateToOnboarding : SplashEvent
}
