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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A smooth animated line chart with gradient fill beneath the line.
 * Supports density-aware line width and dot indicators at data points.
 */
@Composable
fun SimpleLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Dp = 3.dp,
    showDots: Boolean = true,
    showGradientFill: Boolean = true,
) {
    if (dataPoints.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }
    val density = LocalDensity.current
    val lineWidthPx = with(density) { lineWidth.toPx() }
    val dotRadiusPx = with(density) { 4.dp.toPx() }

    val gradientColor = lineColor.copy(alpha = 0.15f)

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

        val padding = lineWidthPx * 2
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val stepX = chartWidth / (dataPoints.size - 1).coerceAtLeast(1)
        val scaleY = chartHeight / range

        val pointsToDraw = (dataPoints.size * animationProgress.value).toInt().coerceAtLeast(1)

        // Calculate points
        val points = mutableListOf<Offset>()
        for (i in 0 until pointsToDraw) {
            val x = padding + i * stepX
            val y = padding + chartHeight - ((dataPoints[i] - minData) * scaleY)
            points.add(Offset(x, y))
        }

        if (points.size < 2) return@Canvas

        // Draw gradient fill below line
        if (showGradientFill && points.size >= 2) {
            val fillPath = Path().apply {
                moveTo(points.first().x, size.height)
                points.forEach { point -> lineTo(point.x, point.y) }
                lineTo(points.last().x, size.height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    startY = points.minOf { it.y },
                    endY = size.height
                )
            )
        }

        // Draw line
        val linePath = Path().apply {
            points.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.x, point.y)
                else lineTo(point.x, point.y)
            }
        }
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = lineWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw dot indicators
        if (showDots && animationProgress.value >= 1f) {
            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = dotRadiusPx,
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = dotRadiusPx * 0.5f,
                    center = point
                )
            }
        }
    }
}
