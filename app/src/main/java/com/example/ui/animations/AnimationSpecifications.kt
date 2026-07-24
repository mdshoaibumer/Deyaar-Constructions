package com.example.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Deyaar Constructions Animation Specifications
 * Material Design 3 compliant animation timings and easing curves
 */
object AnimationSpecs {
    
    // Standard durations (milliseconds)
    object Durations {
        const val MICRO = 100      // Button press, checkbox toggle
        const val SHORT = 200      // Simple transitions, fades
        const val MEDIUM = 300     // Screen transitions, shared elements
        const val LONG = 500       // Complex animations
    }
    
    // Standard easing curves
    object Easings {
        // Material Design 3 standard curves
        val emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)           // Fast out, slow in
        val emphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        val standard = CubicBezierEasing(0.2f, 0f, 0.8f, 0.1f)
        val standardDecelerate = CubicBezierEasing(0f, 0f, 0.2f, 1f)
        val easeInCirc = CubicBezierEasing(0.6f, 0.04f, 0.98f, 0.335f)
        val easeOutCirc = CubicBezierEasing(0.075f, 0.82f, 0.165f, 1f)
    }
    
    // Pre-configured animation specs
    fun buttonPressSpec() = tween<Float>(
        durationMillis = Durations.MICRO,
        easing = Easings.standardDecelerate
    )
    
    fun fadeInSpec() = tween<Float>(
        durationMillis = Durations.SHORT,
        easing = Easings.standard
    )
    
    fun fadeOutSpec() = tween<Float>(
        durationMillis = Durations.SHORT,
        easing = Easings.standard
    )
    
    fun slideInSpec() = tween<IntOffset>(
        durationMillis = Durations.MEDIUM,
        easing = Easings.emphasizedDecelerate
    )
    
    fun slideOutSpec() = tween<IntOffset>(
        durationMillis = Durations.MEDIUM,
        easing = Easings.emphasizedDecelerate
    )
    
    fun scaleSpec() = tween<Float>(
        durationMillis = Durations.SHORT,
        easing = Easings.easeOutCirc
    )
    
    fun sharedElementSpec() = tween<Float>(
        durationMillis = Durations.MEDIUM,
        easing = Easings.emphasized
    )
    
    fun listItemStaggerSpec(index: Int) = tween<Float>(
        durationMillis = Durations.MEDIUM,
        delayMillis = index * 50,  // 50ms stagger between items
        easing = Easings.standard
    )
}

/**
 * Predefined animation states for common scenarios
 */
object AnimationStates {
    
    fun buttonScaleOnPress(): Pair<Float, Float> = 1f to 0.95f
    
    fun cardElevationOnHover(): Pair<Float, Float> = 1f to 4f
    
    fun listItemAlphaOnLoad(): Pair<Float, Float> = 0f to 1f
    
    fun chipBackgroundOnSelect(): Pair<Float, Float> = 0f to 1f
}
