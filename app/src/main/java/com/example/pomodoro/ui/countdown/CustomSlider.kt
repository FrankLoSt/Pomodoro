package com.example.pomodoro.ui.countdown

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberComposeVerticalSliderState(): ComposeVerticalSliderState {
    return remember { ComposeVerticalSliderState() }
}

class ComposeVerticalSliderState {
    var progress by mutableIntStateOf(0)

    fun updateFromTouch(y: Float, height: Int, isFlipped: Boolean = false) {
        val adjustedY = if (isFlipped) height - y else y
        val newProgress = ((1f - adjustedY / height) * 100).toInt().coerceIn(0, 100)
        progress = newProgress
    }

    fun getYFromProgress(height: Int): Float {
        return (1f - progress / 100f) * height
    }
}


@Composable
fun ComposeVerticalSlider(
    state: ComposeVerticalSliderState,
    modifier: Modifier = Modifier,
    width: Dp = 48.dp,
    height: Dp = 200.dp,
    trackColor: Color = Color.LightGray,
    progressColor: Color = Color.Green,
    onProgressChanged: (Int) -> Unit = {},
    onStopTrackingTouch: (Int) -> Unit = {}
) {
    var canvasHeight by remember { mutableStateOf(0) }

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        state.updateFromTouch(event.y, canvasHeight, isFlipped = true) // 👈 flip Y
                        onProgressChanged(state.progress)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        state.updateFromTouch(event.y, canvasHeight, isFlipped = true)
                        onStopTrackingTouch(state.progress)
                        true
                    }
                    else -> false
                }
            }
    ) {
        canvasHeight = size.height.toInt()
        val thumbY = state.getYFromProgress(canvasHeight)

        // Track
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = CornerRadius(12f, 12f)
        )

        // Progress
        drawRect(
            color = progressColor,
            topLeft = Offset(0f, thumbY),
            size = Size(size.width, canvasHeight - thumbY)
        )

        // Thumb
        drawCircle(
            color = Color.Red,
            radius = 12.dp.toPx(),
            center = Offset(size.width / 2, thumbY)
        )
    }
}


