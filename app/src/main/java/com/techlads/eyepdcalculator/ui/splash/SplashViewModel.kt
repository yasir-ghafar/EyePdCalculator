package com.techlads.eyepdcalculator.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techlads.eyepdcalculator.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _events = MutableSharedFlow<SplashEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SplashEvent> = _events.asSharedFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(Constants.SPLASH_DELAY_MS)
            _events.emit(SplashEvent.NavigateToOnboarding)
        }
    }
}
