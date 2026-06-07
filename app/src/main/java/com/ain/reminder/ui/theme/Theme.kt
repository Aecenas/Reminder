package com.ain.reminder.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E7FF),
    onPrimaryContainer = Color(0xFF102B63),
    secondary = Color(0xFF0F766E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7F4EF),
    onSecondaryContainer = Color(0xFF063D39),
    tertiary = Color(0xFFB45309),
    tertiaryContainer = Color(0xFFFFE8C2),
    onTertiaryContainer = Color(0xFF4B2700),
    background = Color(0xFFF6F9FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EEF6),
    onSurface = Color(0xFF162033),
    onSurfaceVariant = Color(0xFF52606F),
    outline = Color(0xFFC7D0DD),
    outlineVariant = Color(0xFFE1E7F0)
)

private val ReminderShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun ReminderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        shapes = ReminderShapes,
        typography = MaterialTheme.typography,
        content = content
    )
}
