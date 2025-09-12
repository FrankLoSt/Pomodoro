package com.example.pomodoro

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.*

@Composable
fun CircularSlider(
    modifier: Modifier = Modifier,
    padding: Float = 50f,
    stroke: Float = 20f,
    cap: StrokeCap = StrokeCap.Round,
    touchStroke: Float = 50f,
    thumbColor: Color = Color.Blue,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    debug: Boolean = false,
    onChange: (Float) -> Unit = {}
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var angle by remember { mutableFloatStateOf(0f) }
    var appliedAngle by remember { mutableFloatStateOf(0f) }

    // Recalculate applied angle whenever angle changes
    LaunchedEffect(angle) {
        var a = angle + 60f
        if (a <= 0f) a += 360f
        appliedAngle = a.coerceIn(0f, 300f)
        onChange(appliedAngle / 300f) // progress in [0,1]
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)
                    val touch = change.position
                    val rad = atan2(center.y - touch.y, center.x - touch.x)
                    angle = Math.toDegrees(rad.toDouble()).toFloat()
                }
            }
    ) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = min(this.size.width, this.size.height) / 2f - padding - stroke / 2f

        // Background arc
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

        // Thumb
        drawCircle(
            color = thumbColor,
            radius = stroke / 2f,
            center = center + Offset(
                radius * cos((120 + appliedAngle) * PI / 180f).toFloat(),
                radius * sin((120 + appliedAngle) * PI / 180f).toFloat()
            )
        )

        if (debug) {
            drawCircle(color = Color.Red, center = center, radius = radius, style = Stroke(2f))
        }
    }
}



@Composable
fun CustomCircularProgressIndicator(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    padding: Float = 50f,
    stroke: Float = 20f,
    cap: StrokeCap = StrokeCap.Round,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
) {
    Canvas(
        modifier = modifier
    ) {
        val size = min(size.width, size.height)
        val radius = size / 2f - padding - stroke / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Background arc (full 300°)
        drawArc(
            color = backgroundColor,
            startAngle = -240f,
            sweepAngle = 300f,
            useCenter = false,
            style = Stroke(stroke, cap = cap),
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2)
        )

        // Progress arc
        drawArc(
            color = progressColor,
            startAngle = 120f,
            sweepAngle = 300f * progress.coerceIn(0f, 1f),
            useCenter = false,
            style = Stroke(stroke, cap = cap),
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}
