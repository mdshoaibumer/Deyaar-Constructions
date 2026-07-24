package com.example.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale

/**
 * Composable wrapper that adds entrance animation to list items
 * Used for staggered animation effects in scrollable lists
 */
@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = AnimationSpecs.listItemStaggerSpec(index),
        label = "listItemAlpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = AnimationSpecs.listItemStaggerSpec(index),
        label = "listItemScale"
    )
    
    Box(
        modifier = modifier
            .alpha(alpha)
            .scale(scale)
    ) {
        content()
    }
}

/**
 * Extension function to wrap composables in animated entrance
 */
inline fun Modifier.animatedListItem(
    index: Int,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.listItemStaggerSpec(index)
): Modifier {
    var isVisible by mutableStateOf(false)
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
        label = "itemAlpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = animationSpec,
        label = "itemScale"
    )
    
    return this
        .alpha(alpha)
        .scale(scale)
}
