package com.techlads.eyepdcalculator.domain

import kotlin.math.hypot

object PdCalculator {

    const val CREDIT_CARD_WIDTH_MM = 85.60f

    fun distanceBetween(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return hypot(x2 - x1, y2 - y1)
    }

    fun pupillaryDistanceMm(
        pupilsDistancePx: Float,
        cardDistancePx: Float,
        referenceWidthMm: Float = CREDIT_CARD_WIDTH_MM
    ): Float {
        require(cardDistancePx > 0f) { "Card distance must be greater than zero" }
        return (referenceWidthMm / cardDistancePx) * pupilsDistancePx
    }
}
