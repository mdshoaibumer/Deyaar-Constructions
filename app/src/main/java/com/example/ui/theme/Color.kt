package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================
// DEYAAR CONSTRUCTION - OFFICIAL PIXEL-PERFECT PALETTE
// ============================================================

// Primary
val PrimaryLight = Color(0xFF002147)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE6F0FA)
val OnPrimaryContainerLight = Color(0xFF001122)

val PrimaryDark = Color(0xFF6699FF)
val OnPrimaryDark = Color(0xFF001133)
val PrimaryContainerDark = Color(0xFF002147)
val OnPrimaryContainerDark = Color(0xFFE6F0FA)

// Secondary
val SecondaryLight = Color(0xFF515F74)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFD5E3FC)
val OnSecondaryContainerLight = Color(0xFF57657A)

val SecondaryDark = Color(0xFFB9C7DF)
val OnSecondaryDark = Color(0xFF3A485B)
val SecondaryContainerDark = Color(0xFF3A485B)
val OnSecondaryContainerDark = Color(0xFFD5E3FC)

// Tertiary
val TertiaryLight = Color(0xFF545F73)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFF909BB1)
val OnTertiaryContainerLight = Color(0xFF283345)

val TertiaryDark = Color(0xFFBCC7DE)
val OnTertiaryDark = Color(0xFF3C475A)
val TertiaryContainerDark = Color(0xFF3C475A)
val OnTertiaryContainerDark = Color(0xFF909BB1)

// Neutrals & Backgrounds
val BackgroundLight = Color(0xFFF7F9FB)
val OnBackgroundLight = Color(0xFF191C1E)
val SurfaceLight = Color(0xFFF7F9FB)
val OnSurfaceLight = Color(0xFF191C1E)
val SurfaceVariantLight = Color(0xFFE0E3E5)
val OnSurfaceVariantLight = Color(0xFF584237)
val OutlineLight = Color(0xFF8C7164)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF2F4F6)
val SurfaceContainerLight = Color(0xFFECEEF0)
val SurfaceContainerHighLight = Color(0xFFE6E8EA)
val SurfaceContainerHighestLight = Color(0xFFE0E3E5)

val BackgroundDark = Color(0xFF111416)
val OnBackgroundDark = Color(0xFFF7F9FB)
val SurfaceDark = Color(0xFF191C1E)
val OnSurfaceDark = Color(0xFFF7F9FB)
val SurfaceVariantDark = Color(0xFF584237)
val OnSurfaceVariantDark = Color(0xFFE0E3E5)
val OutlineDark = Color(0xFFE0C0B1)

// Dark theme surface container hierarchy (proper visual layering)
val SurfaceContainerLowestDark = Color(0xFF0E1012)
val SurfaceContainerLowDark = Color(0xFF1B1E20)
val SurfaceContainerDark = Color(0xFF1F2224)
val SurfaceContainerHighDark = Color(0xFF282B2D)
val SurfaceContainerHighestDark = Color(0xFF333638)

// Semantic Colors (Status)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF93000A)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// ============================================================
// Extended Color System — Semantic Status Colors
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