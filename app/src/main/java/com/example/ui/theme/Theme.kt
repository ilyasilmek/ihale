package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BrandBlue200,
    onPrimary = Navy900,
    primaryContainer = BrandBlue800,
    onPrimaryContainer = BrandBlue200,
    secondary = Slate200,
    onSecondary = Navy900,
    tertiary = GoldAccent,
    background = Navy900,
    surface = Navy800,
    surfaceVariant = Navy700,
    onBackground = Slate100,
    onSurface = Slate100,
    onSurfaceVariant = Slate200,
    error = SeverityCritical
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandBlue700,
    onPrimary = Color.White,
    primaryContainer = BrandBlue200,
    onPrimaryContainer = BrandBlue800,
    secondary = Navy700,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    background = Slate50,
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Navy900,
    onSurface = Navy900,
    onSurfaceVariant = Slate600,
    error = SeverityCritical
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our crisp corporate palette by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

