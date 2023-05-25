package com.github.scribeWizTeam.scribewiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = PrimaryGreen,
    primaryVariant = PrimaryVariantRed,
    secondary = SecondaryWhite,
    background = BackgroundBlack,
    surface = SurfaceDarkGray,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = PrimaryGreen,
    primaryVariant = PrimaryVariantRed,
    secondary = SecondaryWhite,
    background = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun ScribeWizTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}