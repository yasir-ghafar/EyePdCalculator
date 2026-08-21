package com.techlads.eyepdcalculator.ui.calculate

sealed interface CalculatePdEvent {
    data class ShowResult(val pdMm: Float) : CalculatePdEvent
    data object ShowInvalidStateMessage : CalculatePdEvent
}
