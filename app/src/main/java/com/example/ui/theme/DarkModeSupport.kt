package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Premium dark mode support with smooth transitions between light and dark themes
 */

/**
 * Gets the appropriate color based on current theme with smooth animation
 */
@Composable
fun getDynamicColor(
    lightColor: Color,
    darkColor: Color = lightColor
): Color {
    val isDarkTheme = isSystemInDarkTheme()
    
    return animateColorAsState(
        targetValue = if (isDarkTheme) darkColor else lightColor,
        animationSpec = tween(durationMillis = 300),
        label = "theme_color_transition"
    ).value
}

/**
 * Premium surface color that adapts to theme with elevation awareness
 */
@Composable
fun getPremiumSurfaceColor(elevation: Int = 0): Color {
    return when {
        isSystemInDarkTheme() && elevation > 0 -> {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
        isSystemInDarkTheme() -> {
            MaterialTheme.colorScheme.surface
        }
        else -> {
            MaterialTheme.colorScheme.surface
        }
    }
}

/**
 * Gets text color with proper contrast for current theme
 */
@Composable
fun getPremiumTextColor(
    isHeading: Boolean = false
): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface
    }
}

/**
 * Gets secondary text color with proper contrast
 */
@Composable
fun getPremiumSecondaryTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
}

/**
 * Premium divider color that's visible in both themes
 */
@Composable
fun getPremiumDividerColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
}

/**
 * Gets the appropriate border color based on theme and state
 */
@Composable
fun getPremiumBorderColor(isActive: Boolean = false): Color {
    return if (isActive) {
        MaterialTheme.colorScheme.primary
    } else if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        MaterialTheme.colorScheme.outline
    }
}
