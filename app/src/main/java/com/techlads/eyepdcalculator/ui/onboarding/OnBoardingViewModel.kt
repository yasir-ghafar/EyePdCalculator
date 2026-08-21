package com.techlads.eyepdcalculator.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techlads.eyepdcalculator.utils.Constants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnBoardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnBoardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnBoardingEvent> = _events.asSharedFlow()

    fun onPageSelected(position: Int) {
        _uiState.update {
            it.copy(
                currentPage = position,
                isNextVisible = position == Constants.ONBOARDING_LAST_PAGE_INDEX
            )
        }
    }

    fun onNextClicked() {
        viewModelScope.launch {
            _events.emit(OnBoardingEvent.NavigateToHome)
        }
    }
}
