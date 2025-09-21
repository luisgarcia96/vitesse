package com.openclassrooms.vitesse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = ThemePrimary,
  onPrimary = ThemeOnPrimary,
  primaryContainer = ThemePrimaryVariant,
  onPrimaryContainer = ThemeOnPrimary,
  secondary = ThemeSecondary,
  onSecondary = ThemeOnSecondary,
  secondaryContainer = ThemeSecondaryVariant,
  onSecondaryContainer = ThemeOnSecondary,
  tertiary = ThemePrimaryVariant,
  onTertiary = ThemeOnPrimary,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
  primary = ThemePrimary,
  onPrimary = ThemeOnPrimary,
  primaryContainer = ThemePrimaryVariant,
  onPrimaryContainer = ThemeOnPrimary,
  secondary = ThemeSecondary,
  onSecondary = ThemeOnSecondary,
  secondaryContainer = ThemeSecondaryVariant,
  onSecondaryContainer = ThemeOnSecondary,
  tertiary = ThemePrimaryVariant,
  onTertiary = ThemeOnPrimary,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface
)

@Composable
fun VitesseTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as android.app.Activity).window
      window.statusBarColor = colorScheme.primaryContainer.toArgb()
      window.navigationBarColor = colorScheme.primary.toArgb()
      val controller = WindowCompat.getInsetsController(window, view)
      controller.isAppearanceLightStatusBars = !darkTheme
      controller.isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
