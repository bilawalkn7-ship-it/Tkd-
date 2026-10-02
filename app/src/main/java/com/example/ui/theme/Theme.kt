package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TkdColorScheme = darkColorScheme(
  primary = TkdCrimson,
  onPrimary = TkdTextPrimary,
  primaryContainer = TkdCrimsonDark,
  onPrimaryContainer = TkdTextPrimary,
  secondary = TkdBlue,
  onSecondary = TkdTextPrimary,
  secondaryContainer = TkdBlueDark,
  onSecondaryContainer = TkdTextPrimary,
  tertiary = TkdGold,
  onTertiary = TkdDarkBg,
  background = TkdDarkBg,
  onBackground = TkdTextPrimary,
  surface = TkdSurface,
  onSurface = TkdTextPrimary,
  surfaceVariant = TkdSurfaceVariant,
  onSurfaceVariant = TkdTextSecondary,
  outline = TkdSurfaceBorder,
  error = TkdError,
  onError = TkdTextPrimary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark martial arts aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = TkdDarkBg.toArgb()
        window.navigationBarColor = TkdDarkBg.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = TkdColorScheme,
    typography = Typography,
    content = content
  )
}
