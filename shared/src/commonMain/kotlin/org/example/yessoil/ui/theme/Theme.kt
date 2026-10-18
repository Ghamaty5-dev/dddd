package org.example.yessoil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF2E7D5B)
private val LightColors = lightColorScheme(
    primary = Green, onPrimary = Color.White, primaryContainer = Color(0xFFD5F2E1),
    secondary = Color(0xFFDE8B45), tertiary = Color(0xFF467C8A),
    background = Color(0xFFF7FAF7), surface = Color.White,
)

@Composable
fun SoilTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = Typography(), content = content)
}
