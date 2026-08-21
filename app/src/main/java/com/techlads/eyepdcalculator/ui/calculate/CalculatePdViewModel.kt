package com.techlads.eyepdcalculator.ui.calculate

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techlads.eyepdcalculator.domain.PdCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatePdViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageUri: Uri? = savedStateHandle[IMAGE_URI_ARG]

    private val _uiState = MutableStateFlow(
        CalculatePdUiState(imageUri = imageUri)
    )
    val uiState: StateFlow<CalculatePdUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CalculatePdEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CalculatePdEvent> = _events.asSharedFlow()

    private var pupilsDistancePx: Float = 0f
    private var cardDistancePx: Float = 0f

    fun onNextClicked(
        pupilOneX: Float,
        pupilOneY: Float,
        pupilTwoX: Float,
        pupilTwoY: Float,
        cardOneX: Float,
        cardOneY: Float,
        cardTwoX: Float,
        cardTwoY: Float
    ) {
        viewModelScope.launch {
            when (_uiState.value.step) {
                CalculateStep.MARK_PUPILS -> {
                    pupilsDistancePx = PdCalculator.distanceBetween(
                        pupilOneX, pupilOneY, pupilTwoX, pupilTwoY
                    )
                    _uiState.update {
                        it.copy(
                            step = CalculateStep.MARK_CARD,
                            showPupilMarkers = false,
                            showCardMarkers = true
                        )
                    }
                }
                CalculateStep.MARK_CARD -> {
                    cardDistancePx = PdCalculator.distanceBetween(
                        cardOneX, cardOneY, cardTwoX, cardTwoY
                    )
                    if (cardDistancePx <= 0f) {
                        _events.emit(CalculatePdEvent.ShowInvalidStateMessage)
                        return@launch
                    }
                    val pdMm = PdCalculator.pupillaryDistanceMm(
                        pupilsDistancePx = pupilsDistancePx,
                        cardDistancePx = cardDistancePx
                    )
                    _events.emit(CalculatePdEvent.ShowResult(pdMm))
                }
            }
        }
    }

    companion object {
        const val IMAGE_URI_ARG = "imageUri"
    }
}
