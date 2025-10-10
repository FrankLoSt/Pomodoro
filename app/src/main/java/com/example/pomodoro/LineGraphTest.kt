package com.example.pomodoro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LinePlot
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.compareTo
import kotlin.coroutines.cancellation.CancellationException
import kotlin.div
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.text.toInt
import kotlin.times


@Composable
fun LineGraphTest(
    plot: LinePlot, modifier: Modifier = Modifier,
    onSelectionStart: () -> Unit = {},
    onSelectionEnd: () -> Unit = {},
    onSelection: ((Float, List<DataPoint>) -> Unit)? = null
) {
    val paddingTop = plot.paddingTop
    val paddingRight = plot.paddingRight
    val horizontalGap = plot.horizontalExtraSpace
    val isZoomAllowed = plot.isZoomAllowed

    val globalXScale = 1f
    val globalYScale = 1f

    val offset = remember { mutableStateOf(0f) }
    val maxScrollOffset = remember { mutableStateOf(0f) }
    val dragOffset = remember { mutableStateOf(0f) }
    val isDragging = remember { mutableStateOf(false) }
    val xZoom = remember { mutableStateOf(globalXScale) }
    val rowHeight = remember { mutableStateOf(0f) }
    val columnWidth = remember { mutableStateOf(0f) }
    val bgColor = MaterialTheme.colorScheme.surface

    val lines = plot.lines
    val xUnit = plot.xAxis.unit

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        Box(
            modifier = modifier.clipToBounds(),
        ) {
            val points = lines.flatMap { it.dataPoints }
            val (xMin, xMax, xAxisScale) = getXAxisScale(points, plot)
            val (yMin, yMax, yAxisScale) = getYAxisScale(points, plot)

            Canvas(modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .fillMaxWidth()
                .background(bgColor)
                .scrollable(
                    state = rememberScrollableState { delta ->
                        offset.value -= delta
                        if (offset.value < 0f) offset.value = 0f
                        if (offset.value > maxScrollOffset.value) {
                            offset.value = maxScrollOffset.value
                        }
                        delta
                    }, Orientation.Horizontal, enabled = true
                )
                .pointerInput(Unit, Unit) {
                    detectDragZoomGesture(
                        isZoomAllowed = isZoomAllowed,
                        isDragAllowed = plot.selection.enabled,
                        detectDragTimeOut = plot.selection.detectionTime,
                        onDragStart = {
                            dragOffset.value = it.x
                            onSelectionStart()
                            isDragging.value = true
                        }, onDragEnd = {
                            isDragging.value = false
                            onSelectionEnd()
                        }, onZoom = { zoom ->
                            xZoom.value *= zoom
                        }
                    ) { change, _ ->
                        dragOffset.value = change.position.x
                    }
                },
                onDraw = {
                    val xLeft = columnWidth.value + horizontalGap.toPx()
                    val yBottom = size.height - rowHeight.value
                    val xOffset = 20.dp.toPx() * xZoom.value
                    val maxElementInYAxis =
                        getMaxElementInYAxis(yAxisScale, plot.yAxis.steps)
                    val yOffset = ((yBottom - paddingTop.toPx()) / maxElementInYAxis) * globalYScale

                    val xLastPoint =
                        (xMax - xMin) * xOffset * (1 / xUnit) + xLeft + paddingRight.toPx() + horizontalGap.toPx()
                    maxScrollOffset.value = if (xLastPoint > size.width) {
                        xLastPoint - size.width
                    } else 0f

                    val dragLocks = mutableMapOf<LinePlot.Line, Pair<DataPoint, Offset>>()

                    // Draw Grid lines
                    val top = yBottom - ((yMax - yMin) * yOffset)
                    val region =
                        Rect(xLeft, top, size.width - paddingRight.toPx(), yBottom)
                    plot.grid?.draw?.invoke(this, region, xOffset * (1 / xUnit), yOffset)

                    // Draw Lines and Points and AreaUnderLine
                    lines.forEach { line ->
                        val intersection = line.intersection
                        val connection = line.connection
                        val areaUnderLine = line.areaUnderLine

                        // Draw area under curve
                        if (areaUnderLine != null) {
                            val pts = line.dataPoints.map { (x, y) ->
                                val x1 = ((x - xMin) * xOffset * (1 / xUnit)) + xLeft - offset.value
                                val y1 = yBottom - ((y - yMin) * yOffset)
                                Offset(x1, y1)
                            }
                            val p = Path()
                            pts.forEachIndexed { index, offset ->
                                if (index == 0) {
                                    p.moveTo(offset.x, yBottom)
                                }
                                p.lineTo(offset.x, offset.y)
                            }
                            val last = pts.last()
                            val first = pts.first()
                            p.lineTo(last.x, yBottom)
                            p.lineTo(first.x, yBottom)
                            areaUnderLine.draw(this, p)
                        }

                        // Draw Lines and Points
                        var curOffset: Offset? = null
                        var nextOffset: Offset? = null
                        line.dataPoints.forEachIndexed { i, _ ->
                            if (i == 0) {
                                val (x, y) = line.dataPoints[i]
                                val x1 = ((x - xMin) * xOffset * (1 / xUnit)) + xLeft - offset.value
                                val y1 = yBottom - ((y - yMin) * yOffset)
                                curOffset = Offset(x1, y1)
                            }
                            if (line.dataPoints.indices.contains(i + 1)) {
                                val (x, y) = line.dataPoints[i + 1]
                                val x2 = ((x - xMin) * xOffset * (1 / xUnit)) + xLeft - offset.value
                                val y2 = yBottom - ((y - yMin) * yOffset)
                                nextOffset = Offset(x2, y2)
                            }
                            if (nextOffset != null && curOffset != null) {
                                connection?.draw?.invoke(
                                    this,
                                    curOffset!!,
                                    nextOffset!!
                                )
                            }
                            curOffset?.let {
                                if (isDragging.value && isDragLocked(
                                        dragOffset.value,
                                        it,
                                        xOffset
                                    )
                                ) {
                                    dragLocks[line] = line.dataPoints[i] to it
                                } else {
                                    intersection?.draw?.invoke(this, it, line.dataPoints[i])
                                }
                            }
                            curOffset = nextOffset
                        }
                    }

                    // Draw column
                    drawRect(
                        bgColor,
                        Offset(0f, 0f),
                        Size(columnWidth.value, size.height)
                    )

                    // Draw right padding
                    drawRect(
                        bgColor,
                        Offset(size.width - paddingRight.toPx(), 0f),
                        Size(paddingRight.toPx(), size.height)
                    )

                    // Draw drag selection Highlight
                    if (isDragging.value) {
                        // Draw Drag Line highlight
                        dragLocks.values.firstOrNull()?.let { (_, location) ->
                            val (x, _) = location
                            if (x >= columnWidth.value && x <= size.width - paddingRight.toPx()) {
                                plot.selection.highlight?.draw?.invoke(
                                    this,
                                    Offset(x, yBottom),
                                    Offset(x, 0f)
                                )
                            }
                        }
                        // Draw Point Highlight
                        dragLocks.entries.forEach { (line, lock) ->
                            val highlight = line.highlight
                            val location = lock.second
                            val x = location.x
                            if (x >= columnWidth.value && x <= size.width - paddingRight.toPx()) {
                                highlight?.draw?.invoke(this, location)
                            }
                        }
                    }

                    // OnSelection
                    if (isDragging.value) {
                        val x = dragLocks.values.firstOrNull()?.second?.x
                        if (x != null) {
                            onSelection?.invoke(x, dragLocks.values.map { it.first })
                        }
                    }
                })

            GraphXAxis(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(
                        RowClip(
                            columnWidth.value,
                            paddingRight
                        )
                    )
                    .onGloballyPositioned {
                        rowHeight.value = it.size.height.toFloat()
                    }
                    .padding(bottom = plot.xAxis.paddingBottom, top = plot.xAxis.paddingTop),
                columnWidth.value + horizontalGap.value * LocalDensity.current.density,
                offset.value,
                xZoom.value * xAxisScale * (1 / xUnit),
                stepSize = plot.xAxis.stepSize,
            ) {
                plot.xAxis.content(xMin, xAxisScale, xMax)
            }

            GraphYAxis(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .onGloballyPositioned {
                        columnWidth.value = it.size.width.toFloat()
                    }
                    .padding(start = plot.yAxis.paddingStart, end = plot.yAxis.paddingEnd),
                paddingTop = paddingTop.value * LocalDensity.current.density,
                paddingBottom = rowHeight.value,
                scale = globalYScale,
            ) {
                plot.yAxis.content(yMin, yAxisScale, yMax)
            }
        }
    }
}

private fun isDragLocked(dragOffset: Float, it: Offset, xOffset: Float) =
    ((dragOffset) > it.x - xOffset / 2) && ((dragOffset) < it.x + xOffset / 2)

private fun getXAxisScale(
    points: List<DataPoint>,
    plot: LinePlot
): Triple<Float, Float, Float> {
    val xMin = points.minOf { it.x }
    val xMax = points.maxOf { it.x }
    val totalSteps =
        (xMax - xMin)
    val temp = totalSteps / plot.xAxis.steps
    val scale = if (plot.xAxis.roundToInt) ceil(temp) else temp
    return Triple(xMin, xMax, scale)
}

private fun getYAxisScale(
    points: List<DataPoint>,
    plot: LinePlot
): Triple<Float, Float, Float> {
    val steps = plot.yAxis.steps
    val yMin = points.minOf { it.y }
    val yMax = points.maxOf { it.y }

    val totalSteps = (yMax - yMin)
    val temp = totalSteps / if (steps > 1) (steps - 1) else 1

    val scale = if (plot.yAxis.roundToInt) ceil(temp) else temp
    return Triple(yMin, yMax, scale)
}

private fun getMaxElementInYAxis(offset: Float, steps: Int): Float {
    return (if (steps > 1) steps - 1 else 1) * offset
}

private class RowClip(private val leftPadding: Float, private val rightPadding: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                leftPadding,
                0f,
                size.width - rightPadding.value * density.density,
                size.height
            )
        )
    }
}


@Composable
internal fun GraphXAxis(
    modifier: Modifier,
    xStart: Float,
    scrollOffset: Float,
    scale: Float,
    stepSize: Dp,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0))
        }
        val height = placeables.maxOf { it.height }
        layout(constraints.maxWidth, height) {
            var xPos = (xStart - scrollOffset).toInt()
            val step = stepSize.toPx()
            placeables.forEach { placeable ->
                xPos -= (placeable.width / 2f).toInt()
                placeable.place(x = xPos, y = 0)
                xPos += ((step * scale) + (placeable.width / 2f)).toInt()
            }
        }
    }
}

@Composable
internal fun GraphYAxis(
    modifier: Modifier,
    paddingTop: Float,
    paddingBottom: Float,
    scale: Float,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val steps = if (measurables.size <= 1) 1 else measurables.size - 1
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minHeight = 0))
        }
        val width = placeables.maxOf { it.width }
        layout(width, constraints.maxHeight) {
            val yBottom = (constraints.maxHeight - paddingBottom)
            val availableHeight = yBottom - paddingTop
            var yPos = yBottom.toInt()

            placeables.forEach { placeable ->
                yPos -= (placeable.height / 2f).toInt()
                placeable.place(x = 0, y = yPos)
                yPos -= (availableHeight / steps * scale).toInt() - (placeable.height / 2f).toInt()
            }
        }
    }
}



@SuppressWarnings("LoopWithTooManyJumpStatements")
internal suspend fun PointerInputScope.detectDragZoomGesture(
    isZoomAllowed: Boolean = false,
    isDragAllowed: Boolean = true,
    detectDragTimeOut: Long,
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onZoom: (zoom: Float) -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) {
    if (isDragAllowed || isZoomAllowed) {
        forEachGesture {
            val down = awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
            }
            awaitPointerEventScope {
                var zoom = 1f
                var pastTouchSlop = false
                val touchSlop = viewConfiguration.touchSlop

                do {
                    val event = awaitPointerEvent()
                    val canceled = event.changes.any { it.positionChangeConsumed() }
                    if (event.changes.size == 1) {
                        break
                    } else if (event.changes.size == 2) {
                        if (isZoomAllowed) {
                            if (!canceled) {
                                val zoomChange = event.calculateZoom()
                                if (!pastTouchSlop) {
                                    zoom *= zoomChange

                                    val centroidSize =
                                        event.calculateCentroidSize(useCurrent = false)
                                    val zoomMotion = abs(1 - zoom) * centroidSize

                                    if (zoomMotion > touchSlop) {
                                        pastTouchSlop = true
                                    }
                                }

                                if (pastTouchSlop) {
                                    if (zoomChange != 1f) {
                                        onZoom(zoomChange)
                                    }
                                    event.changes.forEach {
                                        if (it.positionChanged()) {
                                            it.consumeAllChanges()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        break
                    }
                } while (!canceled && event.changes.any { it.pressed })
            }

            if (isDragAllowed) {
                try {
                    val drag = awaitLongPressOrCancellation(down, detectDragTimeOut)
                    if (drag != null) {
                        onDragStart.invoke(drag.position)
                        awaitPointerEventScope {
                            if (
                                drag(drag.id) {
                                    onDrag(it, it.positionChange())
                                    it.consumePositionChange()
                                }
                            ) {
                                // consume up if we quit drag gracefully with the up
                                currentEvent.changes.forEach {
                                    if (it.changedToUp()) {
                                        it.consumeDownChange()
                                    }
                                }
                                onDragEnd()
                            } else {
                                onDragEnd()
                            }
                        }
                    }
                } catch (c: CancellationException) {
                    onDragEnd()
                    throw c
                }
            }
        }
    }
}

private suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initialDown: PointerInputChange,
    longPressTimeout: Long
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var currentDown = initialDown
    return try {
        // wait for first tap up or long press
        withTimeout(longPressTimeout) {
            awaitPointerEventScope {
                var finished = false
                while (!finished) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.changes.all { it.changedToUpIgnoreConsumed() }) {
                        // All pointers are up
                        finished = true
                    }

                    if (
                        event.changes.any { it.consumed.downChange || it.isOutOfBounds(size) }
                    ) {
                        finished = true // Canceled
                    }

                    // Check for cancel by position consumption. We can look on the Final pass of
                    // the existing pointer event because it comes after the Main pass we checked
                    // above.
                    val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                    if (consumeCheck.changes.any { it.positionChangeConsumed() }) {
                        finished = true
                    }
                    if (!event.isPointerUp(currentDown.id)) {
                        longPress = event.changes.firstOrNull { it.id == currentDown.id }
                    } else {
                        val newPressed = event.changes.firstOrNull { it.pressed }
                        if (newPressed != null) {
                            currentDown = newPressed
                            longPress = currentDown
                        } else {
                            // should technically never happen as we checked it above
                            finished = true
                        }
                    }
                }
            }
        }
        null
    } catch (_: TimeoutCancellationException) {
        longPress ?: initialDown
    }
}

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.firstOrNull { it.id == pointerId }?.pressed != true
