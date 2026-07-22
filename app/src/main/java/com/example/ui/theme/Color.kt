package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Blueprint Blue (Primary)
val PrimaryLight = Color(0xFF0056D2)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD9E2FF)
val OnPrimaryContainerLight = Color(0xFF001945)

val PrimaryDark = Color(0xFFB0C6FF)
val OnPrimaryDark = Color(0xFF002D71)
val PrimaryContainerDark = Color(0xFF0041A0)
val OnPrimaryContainerDark = Color(0xFFD9E2FF)

// Steel Gray (Secondary)
val SecondaryLight = Color(0xFF525F7F)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFD9E2FF)
val OnSecondaryContainerLight = Color(0xFF0E1C38)

val SecondaryDark = Color(0xFFB9C6EA)
val OnSecondaryDark = Color(0xFF24304D)
val SecondaryContainerDark = Color(0xFF3B4765)
val OnSecondaryContainerDark = Color(0xFFD9E2FF)

// Safety Orange (Tertiary)
val TertiaryLight = Color(0xFFE65100)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDCCF)
val OnTertiaryContainerLight = Color(0xFF350F00)

val TertiaryDark = Color(0xFFFFB598)
val OnTertiaryDark = Color(0xFF591C00)
val TertiaryContainerDark = Color(0xFF822B00)
val OnTertiaryContainerDark = Color(0xFFFFDCCF)

// Neutrals & Backgrounds
val BackgroundLight = Color(0xFFF8F9FA)
val OnBackgroundLight = Color(0xFF1B1B1F)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1B1B1F)
val SurfaceVariantLight = Color(0xFFE1E2EC)
val OnSurfaceVariantLight = Color(0xFF44474F)
val OutlineLight = Color(0xFF74777F)

val BackgroundDark = Color(0xFF121212)
val OnBackgroundDark = Color(0xFFE2E2E6)
val SurfaceDark = Color(0xFF1E1E1E)
val OnSurfaceDark = Color(0xFFE2E2E6)
val SurfaceVariantDark = Color(0xFF44474F)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)
val OutlineDark = Color(0xFF8E9099)

// Semantic Colors (Status)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// ============================================================
// Extended Color System — Semantic Status Colors
// Accessible via DeyaarTheme.colors
// ============================================================

@Immutable
data class DeyaarExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

val LightExtendedColors = DeyaarExtendedColors(
    success = Color(0xFF2E7D32),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFC8E6C9),
    onSuccessContainer = Color(0xFF1B5E20),
    warning = Color(0xFFF57F17),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFF9C4),
    onWarningContainer = Color(0xFF5D4037),
    info = Color(0xFF0277BD),
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFB3E5FC),
    onInfoContainer = Color(0xFF01579B),
)

val DarkExtendedColors = DeyaarExtendedColors(
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF1B5E20),
    successContainer = Color(0xFF2E7D32),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFD54F),
    onWarning = Color(0xFF3E2723),
    warningContainer = Color(0xFF5D4037),
    onWarningContainer = Color(0xFFFFF9C4),
    info = Color(0xFF4FC3F7),
    onInfo = Color(0xFF01579B),
    infoContainer = Color(0xFF0277BD),
    onInfoContainer = Color(0xFFB3E5FC),
)

val LocalDeyaarColors = staticCompositionLocalOf { LightExtendedColors }