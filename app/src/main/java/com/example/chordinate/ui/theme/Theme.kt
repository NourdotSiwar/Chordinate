package com.example.chordinate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.chordinate.backgroundDark
import com.example.chordinate.backgroundDarkHighContrast
import com.example.chordinate.backgroundDarkMediumContrast
import com.example.chordinate.backgroundLight
import com.example.chordinate.backgroundLightHighContrast
import com.example.chordinate.backgroundLightMediumContrast
import com.example.chordinate.errorContainerDark
import com.example.chordinate.errorContainerDarkHighContrast
import com.example.chordinate.errorContainerDarkMediumContrast
import com.example.chordinate.errorContainerLight
import com.example.chordinate.errorContainerLightHighContrast
import com.example.chordinate.errorContainerLightMediumContrast
import com.example.chordinate.errorDark
import com.example.chordinate.errorDarkHighContrast
import com.example.chordinate.errorDarkMediumContrast
import com.example.chordinate.errorLight
import com.example.chordinate.errorLightHighContrast
import com.example.chordinate.errorLightMediumContrast
import com.example.chordinate.inverseOnSurfaceDark
import com.example.chordinate.inverseOnSurfaceDarkHighContrast
import com.example.chordinate.inverseOnSurfaceDarkMediumContrast
import com.example.chordinate.inverseOnSurfaceLight
import com.example.chordinate.inverseOnSurfaceLightHighContrast
import com.example.chordinate.inverseOnSurfaceLightMediumContrast
import com.example.chordinate.inversePrimaryDark
import com.example.chordinate.inversePrimaryDarkHighContrast
import com.example.chordinate.inversePrimaryDarkMediumContrast
import com.example.chordinate.inversePrimaryLight
import com.example.chordinate.inversePrimaryLightHighContrast
import com.example.chordinate.inversePrimaryLightMediumContrast
import com.example.chordinate.inverseSurfaceDark
import com.example.chordinate.inverseSurfaceDarkHighContrast
import com.example.chordinate.inverseSurfaceDarkMediumContrast
import com.example.chordinate.inverseSurfaceLight
import com.example.chordinate.inverseSurfaceLightHighContrast
import com.example.chordinate.inverseSurfaceLightMediumContrast
import com.example.chordinate.onBackgroundDark
import com.example.chordinate.onBackgroundDarkHighContrast
import com.example.chordinate.onBackgroundDarkMediumContrast
import com.example.chordinate.onBackgroundLight
import com.example.chordinate.onBackgroundLightHighContrast
import com.example.chordinate.onBackgroundLightMediumContrast
import com.example.chordinate.onErrorContainerDark
import com.example.chordinate.onErrorContainerDarkHighContrast
import com.example.chordinate.onErrorContainerDarkMediumContrast
import com.example.chordinate.onErrorContainerLight
import com.example.chordinate.onErrorContainerLightHighContrast
import com.example.chordinate.onErrorContainerLightMediumContrast
import com.example.chordinate.onErrorDark
import com.example.chordinate.onErrorDarkHighContrast
import com.example.chordinate.onErrorDarkMediumContrast
import com.example.chordinate.onErrorLight
import com.example.chordinate.onErrorLightHighContrast
import com.example.chordinate.onErrorLightMediumContrast
import com.example.chordinate.onPrimaryContainerDark
import com.example.chordinate.onPrimaryContainerDarkHighContrast
import com.example.chordinate.onPrimaryContainerDarkMediumContrast
import com.example.chordinate.onPrimaryContainerLight
import com.example.chordinate.onPrimaryContainerLightHighContrast
import com.example.chordinate.onPrimaryContainerLightMediumContrast
import com.example.chordinate.onPrimaryDark
import com.example.chordinate.onPrimaryDarkHighContrast
import com.example.chordinate.onPrimaryDarkMediumContrast
import com.example.chordinate.onPrimaryLight
import com.example.chordinate.onPrimaryLightHighContrast
import com.example.chordinate.onPrimaryLightMediumContrast
import com.example.chordinate.onSecondaryContainerDark
import com.example.chordinate.onSecondaryContainerDarkHighContrast
import com.example.chordinate.onSecondaryContainerDarkMediumContrast
import com.example.chordinate.onSecondaryContainerLight
import com.example.chordinate.onSecondaryContainerLightHighContrast
import com.example.chordinate.onSecondaryContainerLightMediumContrast
import com.example.chordinate.onSecondaryDark
import com.example.chordinate.onSecondaryDarkHighContrast
import com.example.chordinate.onSecondaryDarkMediumContrast
import com.example.chordinate.onSecondaryLight
import com.example.chordinate.onSecondaryLightHighContrast
import com.example.chordinate.onSecondaryLightMediumContrast
import com.example.chordinate.onSurfaceDark
import com.example.chordinate.onSurfaceDarkHighContrast
import com.example.chordinate.onSurfaceDarkMediumContrast
import com.example.chordinate.onSurfaceLight
import com.example.chordinate.onSurfaceLightHighContrast
import com.example.chordinate.onSurfaceLightMediumContrast
import com.example.chordinate.onSurfaceVariantDark
import com.example.chordinate.onSurfaceVariantDarkHighContrast
import com.example.chordinate.onSurfaceVariantDarkMediumContrast
import com.example.chordinate.onSurfaceVariantLight
import com.example.chordinate.onSurfaceVariantLightHighContrast
import com.example.chordinate.onSurfaceVariantLightMediumContrast
import com.example.chordinate.onTertiaryContainerDark
import com.example.chordinate.onTertiaryContainerDarkHighContrast
import com.example.chordinate.onTertiaryContainerDarkMediumContrast
import com.example.chordinate.onTertiaryContainerLight
import com.example.chordinate.onTertiaryContainerLightHighContrast
import com.example.chordinate.onTertiaryContainerLightMediumContrast
import com.example.chordinate.onTertiaryDark
import com.example.chordinate.onTertiaryDarkHighContrast
import com.example.chordinate.onTertiaryDarkMediumContrast
import com.example.chordinate.onTertiaryLight
import com.example.chordinate.onTertiaryLightHighContrast
import com.example.chordinate.onTertiaryLightMediumContrast
import com.example.chordinate.outlineDark
import com.example.chordinate.outlineDarkHighContrast
import com.example.chordinate.outlineDarkMediumContrast
import com.example.chordinate.outlineLight
import com.example.chordinate.outlineLightHighContrast
import com.example.chordinate.outlineLightMediumContrast
import com.example.chordinate.outlineVariantDark
import com.example.chordinate.outlineVariantDarkHighContrast
import com.example.chordinate.outlineVariantDarkMediumContrast
import com.example.chordinate.outlineVariantLight
import com.example.chordinate.outlineVariantLightHighContrast
import com.example.chordinate.outlineVariantLightMediumContrast
import com.example.chordinate.primaryContainerDark
import com.example.chordinate.primaryContainerDarkHighContrast
import com.example.chordinate.primaryContainerDarkMediumContrast
import com.example.chordinate.primaryContainerLight
import com.example.chordinate.primaryContainerLightHighContrast
import com.example.chordinate.primaryContainerLightMediumContrast
import com.example.chordinate.primaryDark
import com.example.chordinate.primaryDarkHighContrast
import com.example.chordinate.primaryDarkMediumContrast
import com.example.chordinate.primaryLight
import com.example.chordinate.primaryLightHighContrast
import com.example.chordinate.primaryLightMediumContrast
import com.example.chordinate.scrimDark
import com.example.chordinate.scrimDarkHighContrast
import com.example.chordinate.scrimDarkMediumContrast
import com.example.chordinate.scrimLight
import com.example.chordinate.scrimLightHighContrast
import com.example.chordinate.scrimLightMediumContrast
import com.example.chordinate.secondaryContainerDark
import com.example.chordinate.secondaryContainerDarkHighContrast
import com.example.chordinate.secondaryContainerDarkMediumContrast
import com.example.chordinate.secondaryContainerLight
import com.example.chordinate.secondaryContainerLightHighContrast
import com.example.chordinate.secondaryContainerLightMediumContrast
import com.example.chordinate.secondaryDark
import com.example.chordinate.secondaryDarkHighContrast
import com.example.chordinate.secondaryDarkMediumContrast
import com.example.chordinate.secondaryLight
import com.example.chordinate.secondaryLightHighContrast
import com.example.chordinate.secondaryLightMediumContrast
import com.example.chordinate.surfaceBrightDark
import com.example.chordinate.surfaceBrightDarkHighContrast
import com.example.chordinate.surfaceBrightDarkMediumContrast
import com.example.chordinate.surfaceBrightLight
import com.example.chordinate.surfaceBrightLightHighContrast
import com.example.chordinate.surfaceBrightLightMediumContrast
import com.example.chordinate.surfaceContainerDark
import com.example.chordinate.surfaceContainerDarkHighContrast
import com.example.chordinate.surfaceContainerDarkMediumContrast
import com.example.chordinate.surfaceContainerHighDark
import com.example.chordinate.surfaceContainerHighDarkHighContrast
import com.example.chordinate.surfaceContainerHighDarkMediumContrast
import com.example.chordinate.surfaceContainerHighLight
import com.example.chordinate.surfaceContainerHighLightHighContrast
import com.example.chordinate.surfaceContainerHighLightMediumContrast
import com.example.chordinate.surfaceContainerHighestDark
import com.example.chordinate.surfaceContainerHighestDarkHighContrast
import com.example.chordinate.surfaceContainerHighestDarkMediumContrast
import com.example.chordinate.surfaceContainerHighestLight
import com.example.chordinate.surfaceContainerHighestLightHighContrast
import com.example.chordinate.surfaceContainerHighestLightMediumContrast
import com.example.chordinate.surfaceContainerLight
import com.example.chordinate.surfaceContainerLightHighContrast
import com.example.chordinate.surfaceContainerLightMediumContrast
import com.example.chordinate.surfaceContainerLowDark
import com.example.chordinate.surfaceContainerLowDarkHighContrast
import com.example.chordinate.surfaceContainerLowDarkMediumContrast
import com.example.chordinate.surfaceContainerLowLight
import com.example.chordinate.surfaceContainerLowLightHighContrast
import com.example.chordinate.surfaceContainerLowLightMediumContrast
import com.example.chordinate.surfaceContainerLowestDark
import com.example.chordinate.surfaceContainerLowestDarkHighContrast
import com.example.chordinate.surfaceContainerLowestDarkMediumContrast
import com.example.chordinate.surfaceContainerLowestLight
import com.example.chordinate.surfaceContainerLowestLightHighContrast
import com.example.chordinate.surfaceContainerLowestLightMediumContrast
import com.example.chordinate.surfaceDark
import com.example.chordinate.surfaceDarkHighContrast
import com.example.chordinate.surfaceDarkMediumContrast
import com.example.chordinate.surfaceDimDark
import com.example.chordinate.surfaceDimDarkHighContrast
import com.example.chordinate.surfaceDimDarkMediumContrast
import com.example.chordinate.surfaceDimLight
import com.example.chordinate.surfaceDimLightHighContrast
import com.example.chordinate.surfaceDimLightMediumContrast
import com.example.chordinate.surfaceLight
import com.example.chordinate.surfaceLightHighContrast
import com.example.chordinate.surfaceLightMediumContrast
import com.example.chordinate.surfaceVariantDark
import com.example.chordinate.surfaceVariantDarkHighContrast
import com.example.chordinate.surfaceVariantDarkMediumContrast
import com.example.chordinate.surfaceVariantLight
import com.example.chordinate.surfaceVariantLightHighContrast
import com.example.chordinate.surfaceVariantLightMediumContrast
import com.example.chordinate.tertiaryContainerDark
import com.example.chordinate.tertiaryContainerDarkHighContrast
import com.example.chordinate.tertiaryContainerDarkMediumContrast
import com.example.chordinate.tertiaryContainerLight
import com.example.chordinate.tertiaryContainerLightHighContrast
import com.example.chordinate.tertiaryContainerLightMediumContrast
import com.example.chordinate.tertiaryDark
import com.example.chordinate.tertiaryDarkHighContrast
import com.example.chordinate.tertiaryDarkMediumContrast
import com.example.chordinate.tertiaryLight
import com.example.chordinate.tertiaryLightHighContrast
import com.example.chordinate.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    content = content
  )
}