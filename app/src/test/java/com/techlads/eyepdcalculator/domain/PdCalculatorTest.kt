package com.techlads.eyepdcalculator.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PdCalculatorTest {

    @Test
    fun distanceBetween_isHypotenuse() {
        val distance = PdCalculator.distanceBetween(0f, 0f, 3f, 4f)
        assertEquals(5f, distance, 0.001f)
    }

    @Test
    fun pupillaryDistanceMm_scalesAgainstCardReference() {
        val pd = PdCalculator.pupillaryDistanceMm(
            pupilsDistancePx = 100f,
            cardDistancePx = 200f,
            referenceWidthMm = 85.60f
        )
        assertEquals(42.8f, pd, 0.001f)
    }
}
