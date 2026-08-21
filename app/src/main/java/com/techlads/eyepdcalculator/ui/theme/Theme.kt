package com.techlads.eyepdcalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val VernierDarkColorScheme = darkColorScheme(
    primary = VernierMint,
    onPrimary = VernierOnMint,
    primaryContainer = VernierSurfaceVariant,
    onPrimaryContainer = VernierMint,
    secondary = VernierAmber,
    onSecondary = VernierOnMint,
    secondaryContainer = VernierSurfaceVariant,
    onSecondaryContainer = VernierAmberSoft,
    tertiary = VernierMintDim,
    onTertiary = VernierOnMint,
    background = VernierBackground,
    onBackground = VernierTextPrimary,
    surface = VernierSurface,
    onSurface = VernierTextPrimary,
    surfaceVariant = VernierSurfaceVariant,
    onSurfaceVariant = VernierTextSecondary,
    outline = VernierOutline,
    outlineVariant = VernierOutline,
    error = VernierError,
    onError = VernierOnError,
    scrim = VernierScrim
)

@Immutable
data class VernierExtendedColors(
    val mint: Color = VernierMint,
    val mintDim: Color = VernierMintDim,
    val amber: Color = VernierAmber,
    val amberSoft: Color = VernierAmberSoft,
    val textSecondary: Color = VernierTextSecondary,
    val textTertiary: Color = VernierTextTertiary,
    val surfaceElevated: Color = VernierSurfaceVariant
)

val LocalVernierColors = staticCompositionLocalOf { VernierExtendedColors() }

object VernierTheme {
    val colors: VernierExtendedColors
        @Composable
        get() = LocalVernierColors.current

    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography

    val shapes: androidx.compose.material3.Shapes
        @Composable
        get() = MaterialTheme.shapes
}

@Composable
fun VernierTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalVernierColors provides VernierExtendedColors()) {
        MaterialTheme(
            colorScheme = VernierDarkColorScheme,
            typography = VernierTypography,
            shapes = VernierShapes,
            content = content
        )
    }
}
