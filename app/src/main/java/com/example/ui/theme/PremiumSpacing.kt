package com.example.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Premium spacing utilities for consistent visual hierarchy across the app
 * Follows Material Design 3 spacing scale for pixel-perfect layouts
 */

// Spacer utilities with standard Material Design gaps
@Composable
fun SpaceXSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(Dimens.spaceExtraSmall))
}

@Composable
fun SpaceSmall(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(Dimens.spaceSmall))
}

@Composable
fun SpaceMedium(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(Dimens.spaceMedium))
}

@Composable
fun SpaceLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(Dimens.spaceLarge))
}

@Composable
fun SpaceXLarge(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(Dimens.spaceExtraLarge))
}

/**
 * Provides premium visual separators with Material Design 3 styling
 */
@Composable
fun PremiumDivider() {
    androidx.compose.material3.HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun PremiumVerticalDivider() {
    androidx.compose.material3.VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}
