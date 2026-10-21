package com.example.tilecount.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.domain.math.LengthUnitConverter
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

@Composable
fun TileDiagram(
    surfaceType: SurfaceType,
    surfaceLength: BigDecimal,
    surfaceWidth: BigDecimal,
    surfaceLengthUnit: LengthUnit,
    surfaceWidthUnit: LengthUnit,
    tileLength: BigDecimal,
    tileWidth: BigDecimal,
    tileLengthUnit: LengthUnit,
    tileWidthUnit: LengthUnit,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    // Convert all to meters for visual ratio
    val surfLenM = runCatching { LengthUnitConverter.toMeters(surfaceLength, surfaceLengthUnit) }.getOrNull()
        ?.toDouble() ?: 5.0
    val surfWidM = runCatching { LengthUnitConverter.toMeters(surfaceWidth, surfaceWidthUnit) }.getOrNull()
        ?.toDouble() ?: 4.0
    val tileLenM = runCatching { LengthUnitConverter.toMeters(tileLength, tileLengthUnit) }.getOrNull()
        ?.toDouble() ?: 0.6
    val tileWidM = runCatching { LengthUnitConverter.toMeters(tileWidth, tileWidthUnit) }.getOrNull()
        ?.toDouble() ?: 0.6

    // Target aspect ratio for container (clamped between 0.6 and 1.8 for balanced UI)
    val rawAspect = if (surfaceType == SurfaceType.FLOOR) {
        if (surfWidM > 0) surfLenM / surfWidM else 1.25
    } else {
        // Wall: width over height
        if (surfWidM > 0) surfLenM / surfWidM else 1.25
    }
    val targetAspect = min(1.8, max(0.65, rawAspect)).toFloat()

    val animatedAspect by animateFloatAsState(
        targetValue = targetAspect,
        animationSpec = if (animationsEnabled) tween(300) else tween(0),
        label = "aspect_ratio"
    )

    // Calculate grid line count (illustrative tiles between 2 and 7)
    val cols = max(2, min(7, (surfLenM / max(0.01, tileLenM)).toInt()))
    val rows = max(2, min(7, (surfWidM / max(0.01, tileWidM)).toInt()))

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val tileGridColor = primaryColor.copy(alpha = 0.35f)

    val lengthLabel = if (surfaceType == SurfaceType.FLOOR) {
        "Length: $surfaceLength ${surfaceLengthUnit.symbol}"
    } else {
        "Width: $surfaceLength ${surfaceLengthUnit.symbol}"
    }

    val widthLabel = if (surfaceType == SurfaceType.FLOOR) {
        "Width: $surfaceWidth ${surfaceWidthUnit.symbol}"
    } else {
        "Height: $surfaceWidth ${surfaceWidthUnit.symbol}"
    }

    val tileLabel = "Tile: $tileLength ${tileLengthUnit.symbol} × $tileWidth ${tileWidthUnit.symbol}"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .semantics { contentDescription = "Rectangular surface showing tile layout." },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = lengthLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(animatedAspect)
                .clip(RoundedCornerShape(10.dp))
                .background(surfaceColor)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw outer border of rectangular surface
                drawRoundRect(
                    color = primaryColor,
                    size = size,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw grid lines inside
                val colStep = w / cols
                val rowStep = h / rows

                for (i in 1 until cols) {
                    val x = colStep * i
                    drawLine(
                        color = tileGridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                }

                for (j in 1 until rows) {
                    val y = rowStep * j
                    drawLine(
                        color = tileGridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                }

                // Highlight first tile to show tile dimension visually
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.18f),
                    topLeft = Offset(0f, 0f),
                    size = Size(colStep, rowStep),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(colStep, rowStep),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = widthLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tileLabel,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
