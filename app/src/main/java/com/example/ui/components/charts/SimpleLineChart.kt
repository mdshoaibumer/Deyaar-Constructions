package com.example.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SimpleLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 6f
) {
    if (dataPoints.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxData = dataPoints.maxOrNull() ?: 1f
        val minData = dataPoints.minOrNull() ?: 0f
        val range = (maxData - minData).takeIf { it > 0 } ?: 1f

        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
        val scaleY = size.height / range

        val path = Path()
        val pointsToDraw = (dataPoints.size * animationProgress.value).toInt()

        for (i in 0 until pointsToDraw) {
            val x = i * stepX
            val y = size.height - ((dataPoints[i] - minData) * scaleY)
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = lineWidth)
        )
    }
}
