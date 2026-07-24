package com.example.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * Smooth fade and scale enter transition (appears from center)
 */
fun fadeScaleEnter() = fadeIn(
    animationSpec = tween(durationMillis = 300, delayMillis = 100, easing = FastOutSlowInEasing)
) + scaleIn(
    initialScale = 0.9f,
    animationSpec = tween(durationMillis = 300, delayMillis = 100, easing = FastOutSlowInEasing)
)

/**
 * Smooth fade and scale exit transition
 */
fun fadeScaleExit() = fadeOut(
    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
) + scaleOut(
    targetScale = 0.9f,
    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
)

/**
 * Slide in from bottom transition
 */
fun slideInFromBottom() = slideInVertically(
    initialOffsetY = { it / 2 },
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
) + fadeIn(
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
)

/**
 * Slide out to bottom transition
 */
fun slideOutToBottom() = slideOutVertically(
    targetOffsetY = { it / 2 },
    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
) + fadeOut(
    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
)

/**
 * Slide in from start (left) transition
 */
fun slideInFromStart() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
) + fadeIn(
    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
)

/**
 * Slide out to start (left) transition
 */
fun slideOutToStart() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
) + fadeOut(
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
)

/**
 * Smooth list item animation for lazy lists
 */
@Composable
fun AnimatedVisibilityScope.listItemAnimationModifier(): Modifier {
    return Modifier
        .animateEnterExit(
            enter = slideInFromStart() + expandVertically(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutToStart() + shrinkVertically(
                animationSpec = tween(durationMillis = 250)
            )
        )
}

/**
 * Shared element transition specs for navigation
 */
object SharedElementSpecs {
    const val ENTER_DURATION = 500
    const val EXIT_DURATION = 400
    
    fun sharedEnterTransition() = fadeIn(
        animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing)
    ) + slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing)
    )
    
    fun sharedExitTransition() = fadeOut(
        animationSpec = tween(EXIT_DURATION, easing = FastOutSlowInEasing)
    ) + slideOutVertically(
        targetOffsetY = { -it / 4 },
        animationSpec = tween(EXIT_DURATION, easing = FastOutSlowInEasing)
    )
}
