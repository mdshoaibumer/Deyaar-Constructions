package com.example.ui.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Utility class for consistent haptic feedback across the app.
 * Wraps Compose's haptic feedback API for easy usage in event handlers.
 */
object DeyaarHaptics {

    /**
     * Returns a haptic performer that can be invoked in click handlers.
     *
     * Usage:
     * ```
     * val haptic = rememberHaptic()
     * Button(onClick = { haptic.tick(); doAction() })
     * ```
     */
    @Composable
    fun rememberHaptic(): HapticPerformer {
        val hapticFeedback = LocalHapticFeedback.current
        return HapticPerformer(hapticFeedback)
    }
}

class HapticPerformer(private val hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback) {
    /** Light feedback — button presses, toggle changes */
    fun tick() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    /** Medium feedback — confirming actions, selections */
    fun confirm() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    /** Heavy feedback — errors, destructive actions */
    fun error() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}
