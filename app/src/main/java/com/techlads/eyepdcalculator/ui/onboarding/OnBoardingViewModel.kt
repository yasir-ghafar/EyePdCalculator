package com.techlads.eyepdcalculator.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnBoardingViewModel : ViewModel() {

    private val _events = MutableSharedFlow<OnBoardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnBoardingEvent> = _events.asSharedFlow()

    fun onFinished() {
        viewModelScope.launch {
            _events.emit(OnBoardingEvent.NavigateToHome)
        }
    }
}
