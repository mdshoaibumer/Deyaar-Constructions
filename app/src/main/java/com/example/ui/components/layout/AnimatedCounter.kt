package com.example.ui.components.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
) {
    AnimatedContent(
        targetState = count,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn(tween(300))) togetherWith
                        slideOutVertically { height -> -height } + fadeOut(tween(300))
            } else {
                (slideInVertically { height -> -height } + fadeIn(tween(300))) togetherWith
                        slideOutVertically { height -> height } + fadeOut(tween(300))
            }.using(SizeTransform(clip = false))
        },
        modifier = modifier,
        label = "AnimatedCounter"
    ) { targetCount ->
        Text(
            text = "$targetCount",
            style = style,
            color = color
        )
    }
}
