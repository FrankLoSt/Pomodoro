package com.example.pomodoro.ui.Screen1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// --- CircularSlider.kt (cleaner angle math) ---
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize

@Composable
fun CircularSlider(
    modifier: Modifier = Modifier,
    padding: Float = 50f,
    stroke: Float = 20f,
    cap: StrokeCap = StrokeCap.Round,
    thumbColor: Color = Color.Blue,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    debug: Boolean = false,
    onChange: (Float) -> Unit = {}
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var appliedAngle by remember { mutableFloatStateOf(0f) } // 0..300

    Canvas(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)
                    val touch = change.position

                    // Natural vector from center to touch:
                    val dx = touch.x - center.x


                    val dy = touch.y - center.y


                    // atan2(dy, dx) -> degrees in -180..180, 0 = right, +90 = up, -90 = down

                    val deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()



                    // Normalize to 0..360
                    var deg360 = (deg % 360 + 360) % 360 // safe positive angle

                    // Our arc visually starts at 120° and sweeps 300°
                    // Compute position relative to 120°:
                    val relative = (deg360 - 120f + 360f) % 360f

                    // Only accept values inside 0..300; outside that the user touched outside arc gap
                    val angle = relative.coerceIn(0f, 300f)

                    appliedAngle = angle
                    onChange(appliedAngle / 300f)
                }
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f - padding - stroke / 2f

        // Background arc (300° starting from -240 so it visually starts bottom-left)
        drawArc(
            color = backgroundColor,
            startAngle = -240f,
            sweepAngle = 300f,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            useCenter = false,
            style = Stroke(width = stroke, cap = cap)
        )

        // Progress arc
        drawArc(
            color = progressColor,
            startAngle = 120f,
            sweepAngle = appliedAngle,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            useCenter = false,
            style = Stroke(width = stroke, cap = cap)
        )

        // Thumb position
        val radThumb = Math.toRadians((120f + appliedAngle).toDouble())
        val thumbCenter = center + Offset(
            radius * cos(radThumb).toFloat(),
            radius * sin(radThumb).toFloat()
        )
        drawCircle(color = thumbColor, radius = stroke / 2f, center = thumbCenter)

        if (debug) {
            drawCircle(color = Color.Red, center = center, radius = radius, style = Stroke(2f))
        }
    }
}




// --- CustomCircularProgressIndicator.kt ---
@Composable
fun CustomCircularProgressIndicator(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    blockCount: Int = 200, // number of pixel blocks around circle
    blockSize: Float = 30f, // size of each block (chunky pixels)
    radiusOffset: Float = 50f,
    filledColor: Color = Color(0xFF6B8FD6),
    emptyColor: Color = Color.LightGray
) {
    Canvas(
        modifier = modifier,
    ) {
        val size = min(size.width, size.height)
        val radius = size / 2f - radiusOffset
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        val filledBlocks = (blockCount * progress.coerceIn(0f, 1f)).toInt()

        repeat(blockCount) { i ->
            val angle = (i / blockCount.toFloat()) * 300f + 120f // arc range
            val rad = Math.toRadians(angle.toDouble())

            val x = center.x + radius * cos(rad).toFloat()
            val y = center.y + radius * sin(rad).toFloat()

            val color = if (i < filledBlocks) filledColor else emptyColor

            drawRect(
                color = color,
                topLeft = Offset(x - blockSize / 2f, y - blockSize / 2f),
                size = Size(blockSize, blockSize)
            )
        }
    }
}


