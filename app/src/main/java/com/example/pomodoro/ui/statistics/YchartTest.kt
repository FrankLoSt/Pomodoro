package com.example.pomodoro.ui.statistics

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.getTextBackgroundRect
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.pomodoro.ui.pickmonster.FontSize
import com.example.pomodoro.ui.pickmonster.LocalFontSize
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.theme.PomodoroTheme
import kotlin.math.ceil


@Composable
fun BarChartDayHour(
    pointsData: List<Point> = listOf(
        Point(0f, 30f),
        Point(1f, 45f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
        Point(7f, 50f),
        Point(8f, 65f),
        Point(9f, 80f),
        Point(10f, 40f),
        Point(11f, 55f),
        Point(12f, 70f),
        Point(13f, 35f),
        Point(14f, 60f),
        Point(15f, 85f),
        Point(16f, 25f),
        Point(17f, 95f),
        Point(18f, 50f),
        Point(19f, 40f),
        Point(20f, 70f),
        Point(21f, 65f),
        Point(22f, 30f),
        Point(23f, 55f),
    )
) {


    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val maxWidth: Dp = maxWidth

        val barData = pointsData.map {
            BarData(
                it,
                MaterialTheme.colorScheme.tertiary,
                label = when (it.x.toInt().toString())  {
                    "0" -> "0"
                    "6" -> "6"
                    "12" -> "12"
                    "18" -> "18"
                    "23" -> "23"
                    else -> ""
                }

            )
        }

        val MIN_VISIBLE_HEIGHT = 0.7f // or 0.3f depending on visual scale

        val adjustedBarData = barData.map{ bar ->
           val adjustedY =  if(bar.point.y == 0f) MIN_VISIBLE_HEIGHT else bar.point.y
            bar.copy(point = Point(bar.point.x, adjustedY))
        }

        val steps = adjustedBarData.size - 1

        val stepSize = maxWidth / steps


        // 🔹 X-Axis Configuration
        val xAxisData = AxisData.Builder()
            .axisStepSize(stepSize)
            .steps(steps)
            .labelData { i -> barData[i].label }
            .axisLabelFontSize(14.sp)
            .axisLabelColor(Color(0xFF616161))
            .axisLineColor(Color.Transparent)
            .axisLabelAngle(0f)
            .startDrawPadding(15.dp) // 👈 add initial offset for the first bar
            .build()

        val yMax = pointsData.maxOf { it.y }
        val yScale = yMax / 5
        // 🔹 Y-Axis Configuration
        val yAxisData = AxisData.Builder()
            .steps(5)
            .labelData { i -> (i * yScale).toInt().toString() }
            .axisLineColor(Color.Transparent)
            .axisLabelColor(Color(0xFF757575))
            .axisLabelFontSize(12.sp)
            .axisLabelAngle(0f)
            .axisStepSize(40.dp)
            .topPadding(40.dp)
            .build()

        // 🔹 Bar Styling
        val barStyle = BarStyle(
            barWidth = maxWidth * 0.015f,                        // slightly wider for better spacing
            cornerRadius = 5.dp,                    // smoother edges for a modern look
            paddingBetweenBars = maxWidth * 0.015f,              // consistent breathing room
            isGradientEnabled = true,                // enables gradient rendering
            barBlendMode = BlendMode.SrcOver,        // softer blending
            barDrawStyle = Fill,
            // solid fill
            selectionHighlightData = SelectionHighlightData( // highlight when tapped
                isHighlightBarRequired = true,
                highlightBarColor = Color(0xFF4CAF50),
                highlightBarStrokeWidth = 2.dp,
                highlightBarCornerRadius = 5.dp,
                popUpLabel = { x, y -> "${y.toInt()} M "},

                drawPopUp = { selectedOffset, identifiedPoint, centerPointOfBar, selectedXAxisWidth, barChartType ->

                    // ---- CONFIG ----
                    val highlightTextSize: TextUnit = 12.sp
                    val highlightTextTypeface = Typeface.DEFAULT
                    val highlightTextColor = Color.Red
                    val highlightTextBackgroundColor = Color(0xFFEEE69B)
                    val highlightTextBackgroundAlpha = 0.9f
                    val highlightLabelAlignment: Paint.Align = Paint.Align.CENTER
                    val highlightTextOffset: Dp = 10.dp
                    val backgroundColorFilter: ColorFilter? = null
                    val backgroundStyle: DrawStyle = Fill
                    val backgroundBlendMode: BlendMode = BlendMode.SrcOver
                    val highlightPopUpCornerRadius: CornerRadius = CornerRadius(8f)

                    val highlightTextPaint = TextPaint().apply {
                        textSize = highlightTextSize.toPx()
                        color = highlightTextColor.toArgb()
                        textAlign = highlightLabelAlignment
                        typeface = highlightTextTypeface
                    }

                    val popUpLabel: (Float, Float) -> String = { _, y -> "${y.toInt()} M" }
                    val label = popUpLabel(identifiedPoint.point.x, identifiedPoint.point.y)

                    drawContext.canvas.nativeCanvas.apply {

                        // ---- Horizontal positioning ----
                        val safeLeftOffset = 8.dp.toPx()
                        val safeRightOffset = size.width - 8.dp.toPx()

                        // Prevent popup from clipping on the left/right edges
                        val x = when {
                            centerPointOfBar < safeLeftOffset * 2 -> safeLeftOffset * 2
                            centerPointOfBar > safeRightOffset -> safeRightOffset - safeLeftOffset
                            else -> centerPointOfBar
                        }

                        // ---- Vertical positioning ----
                        // Always draw near the top of the chart (fixed Y)
                        val chartTopPadding = 24.dp.toPx()
                        val y = chartTopPadding

                        // Measure background rectangle based on text size
                        val background = getTextBackgroundRect(x, y, label, highlightTextPaint)

                        // ---- Draw popup background ----
                        drawRoundRect(
                            color = highlightTextBackgroundColor,
                            topLeft = Offset(
                                background.left.toFloat(),
                                background.top.toFloat()
                            ),
                            size = Size(
                                background.width().toFloat(),
                                background.height().toFloat()
                            ),
                            alpha = highlightTextBackgroundAlpha,
                            cornerRadius = highlightPopUpCornerRadius,
                            colorFilter = backgroundColorFilter,
                            blendMode = backgroundBlendMode,
                            style = backgroundStyle,
                        )

                        // ---- Center text inside the box ----
                        val textY = background.centerY() -
                                (highlightTextPaint.fontMetrics.descent + highlightTextPaint.fontMetrics.ascent) / 2

                        drawText(label, x, textY, highlightTextPaint)
                    }
                }

            ),

            )


        // 🔹 Bar Chart Data
        val barChartData = BarChartData(
            chartData = adjustedBarData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color.White,
            horizontalExtraSpace = 10.dp,
            barStyle = barStyle,
            paddingTop = 24.dp,
            paddingEnd = 16.dp,
            tapPadding = 12.dp,
            showYAxis = true,
            showXAxis = true,
            barChartType = BarChartType.VERTICAL,

            )

        // 🔹 Chart Layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // make the card white
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White) // inner area also whi
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        barChartData = barChartData,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun BarDayPreview () {
    MyAppTheme {
        BarChartDayHour()
    }
}



@Preview (
    widthDp = 900,
    heightDp = 412
)
@Composable
fun BarDayPreview2 () {
    MyAppTheme {
        BarChartDayHour()
    }
}




@Composable
fun BarChartWeekDay(
    pointsData: List<Point> = listOf(
        Point(0f, 30f),
        Point(1f, 45f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
    )
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {

        val fontSize: FontSize = LocalFontSize.current

        val maxWidth: Dp = maxWidth
        val maxHeight: Dp = maxHeight


        val barData = pointsData.map {
            BarData(
                it,
                MaterialTheme.colorScheme.tertiary,
                label = when (it.x.toInt())  {
                    0 -> "Mon"
                    1 -> "Tue"
                    2 -> "Wed"
                    3 -> "Thu"
                    4 -> "Fri"
                    5 -> "Sat"
                    6 -> "Sun"
                    else -> ""
                }
            )
        }

        val MIN_VISIBLE_HEIGHT = 0.7f // or 0.3f depending on visual scale

        val adjustedBarData = barData.map{ bar ->
            val adjustedY =  if(bar.point.y == 0f) MIN_VISIBLE_HEIGHT else bar.point.y
            bar.copy(point = Point(bar.point.x, adjustedY))
        }

        val steps = adjustedBarData.size - 1

        val stepSize = maxWidth / steps

        // 🔹 X-Axis Configuration
        val xAxisData = AxisData.Builder()
            .axisStepSize(stepSize)
            .steps(steps)
            .labelData { i -> barData[i].label }
            .axisLabelFontSize(fontSize.medium)
            .axisLabelColor(Color(0xFF616161))
            .axisLineColor(Color.Transparent)
            .axisLabelAngle(0f)
            .startDrawPadding(maxWidth * 0.05f) // 👈 add initial offset for the first bar
            .build()

        val yMax = pointsData.maxOf { it.y }
        val yScale = yMax / 5
        // 🔹 Y-Axis Configuration
        val yAxisData = AxisData.Builder()
            .steps(5)
            .labelData { i -> if(yScale >0) (i * yScale).toInt().toString() else {
                when (i) {
                    0 -> "0"
                    1 -> "5"
                    2 -> "10"
                    3 -> "15"
                    4 -> "20"
                    5 -> "25"
                    else -> ""
                }
            } }
            .axisLineColor(Color.Transparent)
            .axisLabelColor(Color(0xFF757575))
            .axisLabelFontSize(12.sp)
            .axisLabelAngle(0f)
            .axisStepSize(maxHeight / 6)
            .topPadding(40.dp)
            .build()

        // 🔹 Bar Styling
        val barStyle = BarStyle(
            barWidth = (maxWidth * 0.09f),                        // slightly wider for better spacing
            cornerRadius = 5.dp,                    // smoother edges for a modern look
            paddingBetweenBars =(maxWidth * 0.02f),              // consistent breathing room
            isGradientEnabled = true,                // enables gradient rendering
            barBlendMode = BlendMode.SrcOver,        // softer blending
            barDrawStyle = Fill,

            // solid fill
            selectionHighlightData = SelectionHighlightData( // highlight when tapped
                isHighlightBarRequired = true,
                highlightBarColor = Color(0xFF4CAF50),
                highlightBarStrokeWidth = 1.dp,
                highlightBarCornerRadius = 5.dp,
                popUpLabel = { x, y -> "${y.toInt()} M "}
            ),

            )


        // 🔹 Bar Chart Data
        val barChartData = BarChartData(
            chartData = adjustedBarData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color.White,
            horizontalExtraSpace = 10.dp,
            barStyle = barStyle,
            paddingTop = 24.dp,
            paddingEnd = 16.dp,
            tapPadding = 12.dp,
            showYAxis = true,
            showXAxis = true,
            barChartType = BarChartType.VERTICAL,
            )

        // 🔹 Chart Layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // make the card white
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White) // inner area also whi
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        barChartData = barChartData,
                    )
                }
            }
        }
    }
}




@Composable
fun BarChartMonthDay (
    pointsData: List<Point> = listOf(
        Point(1f, 245f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
        Point(7f, 150f),
        Point(8f, 165f),
        Point(9f, 180f),
        Point(10f, 140f),
        Point(11f, 322f),
        Point(12f, 70f),
        Point(13f, 35f),
        Point(14f, 60f),
        Point(15f, 85f),
        Point(16f, 600f),
        Point(17f, 500f),
        Point(18f, 432f),
        Point(19f, 401f),
        Point(20f, 342f),
        Point(21f, 124f),
        Point(22f, 130f),
        Point(23f, 155f),
        Point(24f, 145f),
        Point(25f, 60f),
        Point(26f, 35f),
        Point(27f, 280f),
        Point(28f, 320f),
        Point(29f, 190f),
        Point(30f, 75f),
        //Point(31f, 700f)
    )
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {

        val fontSize: FontSize = LocalFontSize.current

        val maxWidth: Dp = maxWidth
        val maxHeight: Dp = maxHeight


        val barData = pointsData.map {
            BarData(
                it,
                MaterialTheme.colorScheme.tertiary,
                label =it.x.toInt().toString()
            )
        }

        val MIN_VISIBLE_HEIGHT = 0.7f // or 0.3f depending on visual scale

        val adjustedBarData = barData.map{ bar ->
            val adjustedY =  if(bar.point.y == 0f) MIN_VISIBLE_HEIGHT else bar.point.y
            bar.copy(point = Point(bar.point.x, adjustedY))
        }

        val steps = adjustedBarData.size - 1

        val stepSize = maxWidth / steps

        // 🔹 X-Axis Configuration
        val xAxisData = AxisData.Builder()
            .axisStepSize(stepSize)
            .steps(steps)
            .labelData { i -> when (pointsData.size) {
                30 -> {
                    when (i + 1) {
                        1 -> "  1"
                        5 -> "5"
                        10 -> "10"
                        15 -> "15"
                        20 -> "20"
                        25 -> "25"
                        30 -> "30"
                        else -> ""
                    }
                }

                31 -> {
                    when (i + 1) {
                        1 -> "  1"
                        5 -> "5"
                        10 -> "10"
                        15 -> "15"
                        20 -> "20"
                        25 -> "25"
                        31 -> "31"
                        else -> ""
                    }
                }

                28 -> {
                    when (i + 1) {
                        1 -> "  1"
                        5 -> "5"
                        10 -> "10"
                        15 -> "15"
                        20 -> "20"
                        25 -> "25"
                        28 -> "28"
                        else -> ""
                    }
                }

                else -> {
                    when (i + 1) {
                        1 -> "  1"
                        5 -> "5"
                        10 -> "10"
                        15 -> "15"
                        20 -> "20"
                        25 -> "25"
                        29 -> "29"
                        else -> ""
                    }
                }
            }}
            .axisLabelFontSize(fontSize.medium)
            .axisLabelColor(Color(0xFF616161))
            .axisLineColor(Color.Transparent)
            .axisLabelAngle(0f)
            .startDrawPadding(maxWidth * 0.05f) // 👈 add initial offset for the first bar
            .build()

        val yMax = pointsData.maxOf { it.y }
        val yScale = yMax / 5
        // 🔹 Y-Axis Configuration
        val yAxisData = AxisData.Builder()
            .steps(5)
            .labelData { i -> if(yScale >0) (i * yScale).toInt().toString() else {
                when (i) {
                    0 -> "0"
                    1 -> "5"
                    2 -> "10"
                    3 -> "15"
                    4 -> "20"
                    5 -> "25"
                    else -> ""
                }
            } }
            .axisLineColor(Color.Transparent)
            .axisLabelColor(Color(0xFF757575))
            .axisLabelFontSize(12.sp)
            .axisLabelAngle(0f)
            .axisStepSize(maxHeight / 6)
            .topPadding(40.dp)
            .build()

        // 🔹 Bar Styling
        val barStyle = BarStyle(
            barWidth = (maxWidth * 0.015f),                        // slightly wider for better spacing
            cornerRadius = 5.dp,                    // smoother edges for a modern look
            paddingBetweenBars =(maxWidth * 0.008f),              // consistent breathing room
            isGradientEnabled = true,                // enables gradient rendering
            barBlendMode = BlendMode.SrcOver,        // softer blending
            barDrawStyle = Fill,

            // solid fill
            selectionHighlightData = SelectionHighlightData( // highlight when tapped
                isHighlightBarRequired = true,
                highlightBarColor = Color(0xFF4CAF50),
                highlightBarStrokeWidth = 2.dp,
                highlightBarCornerRadius = 5.dp,
                popUpLabel = { x, y -> "${y.toInt()} M "},

                drawPopUp = { selectedOffset, identifiedPoint, centerPointOfBar, selectedXAxisWidth, barChartType ->

                    // ---- CONFIG ----
                    val highlightTextSize: TextUnit = 12.sp
                    val highlightTextTypeface = Typeface.DEFAULT
                    val highlightTextColor = Color.Red
                    val highlightTextBackgroundColor = Color(0xFFEEE69B)
                    val highlightTextBackgroundAlpha = 0.9f
                    val highlightLabelAlignment: Paint.Align = Paint.Align.CENTER
                    val highlightTextOffset: Dp = 10.dp
                    val backgroundColorFilter: ColorFilter? = null
                    val backgroundStyle: DrawStyle = Fill
                    val backgroundBlendMode: BlendMode = BlendMode.SrcOver
                    val highlightPopUpCornerRadius: CornerRadius = CornerRadius(8f)

                    val highlightTextPaint = TextPaint().apply {
                        textSize = highlightTextSize.toPx()
                        color = highlightTextColor.toArgb()
                        textAlign = highlightLabelAlignment
                        typeface = highlightTextTypeface
                    }

                    val popUpLabel: (Float, Float) -> String = { _, y -> "${y.toInt()} M" }
                    val label = popUpLabel(identifiedPoint.point.x, identifiedPoint.point.y)

                    drawContext.canvas.nativeCanvas.apply {

                        // ---- Horizontal positioning ----
                        val safeLeftOffset = 8.dp.toPx()
                        val safeRightOffset = size.width - 8.dp.toPx()

                        // Prevent popup from clipping on the left/right edges
                        val x = when {
                            centerPointOfBar < safeLeftOffset * 2 -> safeLeftOffset * 2
                            centerPointOfBar > safeRightOffset -> safeRightOffset - safeLeftOffset
                            else -> centerPointOfBar
                        }

                        // ---- Vertical positioning ----
                        // Always draw near the top of the chart (fixed Y)
                        val chartTopPadding = 24.dp.toPx()
                        val y = chartTopPadding

                        // Measure background rectangle based on text size
                        val background = getTextBackgroundRect(x, y, label, highlightTextPaint)

                        // ---- Draw popup background ----
                        drawRoundRect(
                            color = highlightTextBackgroundColor,
                            topLeft = Offset(
                                background.left.toFloat(),
                                background.top.toFloat()
                            ),
                            size = Size(
                                background.width().toFloat(),
                                background.height().toFloat()
                            ),
                            alpha = highlightTextBackgroundAlpha,
                            cornerRadius = highlightPopUpCornerRadius,
                            colorFilter = backgroundColorFilter,
                            blendMode = backgroundBlendMode,
                            style = backgroundStyle,
                        )

                        // ---- Center text inside the box ----
                        val textY = background.centerY() -
                                (highlightTextPaint.fontMetrics.descent + highlightTextPaint.fontMetrics.ascent) / 2

                        drawText(label, x, textY, highlightTextPaint)
                    }
                }

            ),

            )


        // 🔹 Bar Chart Data
        val barChartData = BarChartData(
            chartData = adjustedBarData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color.White,
            horizontalExtraSpace = 10.dp,
            barStyle = barStyle,
            paddingTop = 24.dp,
            paddingEnd = 16.dp,
            tapPadding = 12.dp,
            showYAxis = true,
            showXAxis = true,
            barChartType = BarChartType.VERTICAL,
        )

        // 🔹 Chart Layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // make the card white
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White) // inner area also whi
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        barChartData = barChartData,
                    )
                }
            }
        }
    }
}






@Preview
@Composable
fun MonthPreview2() {
    PomodoroTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            BarChartMonthDay()
        }
    }
}



@Preview(
    widthDp = 900,
    heightDp = 400
)
@Composable
fun MonthPreview () {
    PomodoroTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            BarChartMonthDay()
        }
    }
}






@Composable
fun ChartYearMonth(
    pointsData: List<Point> =  listOf(
        Point(1f, 45f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
        Point(7f, 50f),
        Point(8f, 65f),
        Point(9f, 80f),
        Point(10f, 40f),
        Point(11f, 55f),
        Point(12f, 70f)
        //Point(31f, 700f)
    )
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val chartWidth = maxWidth
        val stepCount = pointsData.size - 1
        val dynamicStepSize = chartWidth / stepCount
        val steps = 5

        val pointsData = pointsData

        val xAxisData = AxisData.Builder()
            .axisStepSize(dynamicStepSize)
            .backgroundColor(Color.Transparent)
            .steps(stepCount)
            .startPadding(25.dp)
            .labelData { i ->
                when (i + 1) {
                    1 -> "   1"
                    2 -> "2"
                    3 -> "3"
                    4 -> "4"
                    5 -> "5"
                    6 -> "6"
                    7 -> "7"
                    8 -> "8"
                    9 -> "9"
                    10 -> "10"
                    11 -> "11"
                    12 -> "12"
                    else -> ""
                }
            }
            .labelAndAxisLinePadding(12.dp)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()

        val yMax = pointsData.maxOf { it.y }
        val yScale = yMax / steps

        val yAxisData = AxisData.Builder()
            .axisStepSize(100.dp)
            .steps(5)
            .backgroundColor(Color.Transparent)
            .labelData { i -> (i * yScale).toInt().toString() }
            .labelAndAxisLinePadding(35.dp)
            .axisLineColor(Color.Transparent)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)

            .build()

        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = pointsData,
                        LineStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            lineType = LineType.SmoothCurve(isDotted = false)
                        ),
                        IntersectionPoint(
                            color = MaterialTheme.colorScheme.tertiary,
                            radius = 3.dp
                        ),
                        SelectionHighlightPoint(
                            color = MaterialTheme.colorScheme.tertiary,
                        ),
                        ShadowUnderLine(
                            alpha = 0.5f,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary,
                                    Color.Transparent
                                )
                            )
                        ),
                        SelectionHighlightPopUp()
                    )
                ),
            ),
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines(
                color = Color.LightGray,
                lineWidth = 1.dp,
                enableHorizontalLines = true,
                enableVerticalLines = false,
            )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds(), // prevents overflow
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(300.dp, 400.dp),
                lineChartData = lineChartData,
            )
        }
    }
}


@Composable
fun BarChartYearMonth(
    pointsData: List<Point> = listOf(
        Point(1f, 45f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
        Point(7f, 50f),
        Point(8f, 65f),
        Point(9f, 80f),
        Point(10f, 40f),
        Point(11f, 55f),
        Point(12f, 70f)
        //Point(31f, 700f)
    )
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {

        val fontSize: FontSize = LocalFontSize.current

        val maxWidth: Dp = maxWidth
        val maxHeight: Dp = maxHeight


        val barData = pointsData.map {
            BarData(
                it,
                MaterialTheme.colorScheme.tertiary,
                label =it.x.toInt().toString()
            )
        }

        val MIN_VISIBLE_HEIGHT = 0.7f // or 0.3f depending on visual scale

        val adjustedBarData = barData.map{ bar ->
            val adjustedY =  if(bar.point.y == 0f) MIN_VISIBLE_HEIGHT else bar.point.y
            bar.copy(point = Point(bar.point.x, adjustedY))
        }

        val steps = adjustedBarData.size - 1

        val stepSize = maxWidth / steps

        // 🔹 X-Axis Configuration
        val xAxisData = AxisData.Builder()
            .axisStepSize(stepSize)
            .steps(steps)
            .labelData { i ->
                when (i + 1) {
                    1 -> "   1"
                    2 -> "2"
                    3 -> "3"
                    4 -> "4"
                    5 -> "5"
                    6 -> "6"
                    7 -> "7"
                    8 -> "8"
                    9 -> "9"
                    10 -> "10"
                    11 -> "11"
                    12 -> "12"
                    else -> ""
                }
            }
            .axisLabelFontSize(fontSize.medium)
            .axisLabelColor(Color(0xFF616161))
            .axisLineColor(Color.Transparent)
            .axisLabelAngle(0f)
            .startDrawPadding(maxWidth * 0.05f) // 👈 add initial offset for the first bar
            .build()

        val yMax = pointsData.maxOf { it.y }
        val yScale = yMax / 5
        // 🔹 Y-Axis Configuration
        val yAxisData = AxisData.Builder()
            .steps(5)
            .labelData { i -> if(yScale >0) (i * yScale).toInt().toString() else {
                when (i) {
                    0 -> "0"
                    1 -> "5"
                    2 -> "10"
                    3 -> "15"
                    4 -> "20"
                    5 -> "25"
                    else -> ""
                }
            } }
            .axisLineColor(Color.Transparent)
            .axisLabelColor(Color(0xFF757575))
            .axisLabelFontSize(12.sp)
            .axisLabelAngle(0f)
            .axisStepSize(maxHeight / 6)
            .topPadding(40.dp)
            .build()

        // 🔹 Bar Styling
        val barStyle = BarStyle(
            barWidth = (maxWidth * 0.05f),                        // slightly wider for better spacing
            cornerRadius = 5.dp,                    // smoother edges for a modern look
            paddingBetweenBars =(maxWidth * 0.01f),              // consistent breathing room
            isGradientEnabled = true,                // enables gradient rendering
            barBlendMode = BlendMode.SrcOver,        // softer blending
            barDrawStyle = Fill,

            // solid fill
            selectionHighlightData = SelectionHighlightData( // highlight when tapped
                isHighlightBarRequired = true,
                highlightBarColor = Color(0xFF4CAF50),
                highlightBarStrokeWidth = 2.dp,
                highlightBarCornerRadius = 5.dp,
                popUpLabel = { x, y -> "${y.toInt()} M "},

                drawPopUp = { selectedOffset, identifiedPoint, centerPointOfBar, selectedXAxisWidth, barChartType ->

                    // ---- CONFIG ----
                    val highlightTextSize: TextUnit = 12.sp
                    val highlightTextTypeface = Typeface.DEFAULT
                    val highlightTextColor = Color.Red
                    val highlightTextBackgroundColor = Color(0xFFEEE69B)
                    val highlightTextBackgroundAlpha = 0.9f
                    val highlightLabelAlignment: Paint.Align = Paint.Align.CENTER
                    val highlightTextOffset: Dp = 10.dp
                    val backgroundColorFilter: ColorFilter? = null
                    val backgroundStyle: DrawStyle = Fill
                    val backgroundBlendMode: BlendMode = BlendMode.SrcOver
                    val highlightPopUpCornerRadius: CornerRadius = CornerRadius(8f)

                    val highlightTextPaint = TextPaint().apply {
                        textSize = highlightTextSize.toPx()
                        color = highlightTextColor.toArgb()
                        textAlign = highlightLabelAlignment
                        typeface = highlightTextTypeface
                    }

                    val popUpLabel: (Float, Float) -> String = { _, y -> "${y.toInt()} M" }
                    val label = popUpLabel(identifiedPoint.point.x, identifiedPoint.point.y)

                    drawContext.canvas.nativeCanvas.apply {

                        // ---- Horizontal positioning ----
                        val safeLeftOffset = 8.dp.toPx()
                        val safeRightOffset = size.width - 8.dp.toPx()

                        // Prevent popup from clipping on the left/right edges
                        val x = when {
                            centerPointOfBar < safeLeftOffset * 2 -> safeLeftOffset * 2
                            centerPointOfBar > safeRightOffset -> safeRightOffset - safeLeftOffset
                            else -> centerPointOfBar
                        }

                        // ---- Vertical positioning ----
                        // Always draw near the top of the chart (fixed Y)
                        val chartTopPadding = 24.dp.toPx()
                        val y = chartTopPadding

                        // Measure background rectangle based on text size
                        val background = getTextBackgroundRect(x, y, label, highlightTextPaint)

                        // ---- Draw popup background ----
                        drawRoundRect(
                            color = highlightTextBackgroundColor,
                            topLeft = Offset(
                                background.left.toFloat(),
                                background.top.toFloat()
                            ),
                            size = Size(
                                background.width().toFloat(),
                                background.height().toFloat()
                            ),
                            alpha = highlightTextBackgroundAlpha,
                            cornerRadius = highlightPopUpCornerRadius,
                            colorFilter = backgroundColorFilter,
                            blendMode = backgroundBlendMode,
                            style = backgroundStyle,
                        )

                        // ---- Center text inside the box ----
                        val textY = background.centerY() -
                                (highlightTextPaint.fontMetrics.descent + highlightTextPaint.fontMetrics.ascent) / 2

                        drawText(label, x, textY, highlightTextPaint)
                    }
                }

            ),

            )


        // 🔹 Bar Chart Data
        val barChartData = BarChartData(
            chartData = adjustedBarData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color.White,
            horizontalExtraSpace = 10.dp,
            barStyle = barStyle,
            paddingTop = 24.dp,
            paddingEnd = 16.dp,
            tapPadding = 12.dp,
            showYAxis = true,
            showXAxis = true,
            barChartType = BarChartType.VERTICAL,
        )

        // 🔹 Chart Layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // make the card white
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White) // inner area also whi
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        barChartData = barChartData,
                    )
                }
            }
        }
    }
}

@Preview()
@Composable()
fun YeahMonthPreview () {
    PomodoroTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            BarChartYearMonth()
        }
    }
}