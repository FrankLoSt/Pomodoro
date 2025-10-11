package com.example.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.ShadowUnderLine

import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.GridLines
import com.example.pomodoro.data.datastore.ChartState
import com.example.pomodoro.ui.theme.PomodoroTheme

@Composable
fun ChartDayHour (
    pointsData: List<Point> =  listOf(
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
    val steps = 5
    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(15.3.dp)
        .backgroundColor(Color.Transparent)
        .steps(23)
        .labelData { i ->
            when (i) {
                0 -> "  0"
                6 -> "6"
                12 -> "12"
                18 -> "18"
                23 -> "23  "
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
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(Color.Black)
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
            color = MaterialTheme.colorScheme.tertiary,
            lineWidth = 1.dp,
            enableHorizontalLines = true,
            enableVerticalLines = false,
        )
    )

    Card(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .clipToBounds(), // prevents overflow
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData,
        )
    }
}

@Preview
@Composable
fun ChartDayHourReview () {
    PomodoroTheme {
        ChartDayHour()
    }
}


@Composable
fun ChartWeekDay (
    pointsData: List<Point> =  listOf(
        Point(0f, 30f),
        Point(1f, 45f),
        Point(2f, 60f),
        Point(3f, 30f),
        Point(4f, 90f),
        Point(5f, 20f),
        Point(6f, 75f),
    )
) {
    val steps = 5
    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(58.dp)
        .backgroundColor(Color.Transparent)
        .steps(7)
        .labelData { i ->  when (i) {
            0 -> "        Mon"
            1 -> "Tue"
            2 -> "Wed"
            3 -> "Thu"
            4 -> "Fri"
            5 -> "Sat"
            6 -> "Sun"
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
        .labelAndAxisLinePadding(20.dp)
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
            .height(300.dp)
            .fillMaxWidth()
            .clipToBounds(), // prevents overflow
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData,
        )
    }


}

@Preview
@Composable 
fun ChartWeekDayReview () {
    PomodoroTheme {
        ChartWeekDay()
    }
}




@Composable
fun ChartMonthDay (
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
        Point(24f, 45f),
        Point(25f, 60f),
        Point(26f, 35f),
        Point(27f, 80f),
        Point(28f, 20f),
        Point(29f, 90f),
        Point(30f, 75f),
        //Point(31f, 700f)
    )
) {
    val steps = 5

    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(11.64.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i ->
            when (pointsData.size) {
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
                    when ( i+1) {
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
        .labelAndAxisLinePadding(20.dp)
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
            .height(300.dp)
            .fillMaxWidth()
            .clipToBounds(), // prevents overflow
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData,
        )
    }


}

@Preview
@Composable
fun ChartMonthReview () {
    PomodoroTheme {
        ChartMonthDay()
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

    val steps = 5

    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(34.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .startPadding(25.dp)
        .labelData { i -> when (i + 1) {
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
            .height(300.dp)
            .fillMaxWidth()
            .clipToBounds(), // prevents overflow
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData,
        )
    }
}

@Preview
@Composable
fun CharYearReview () {
    PomodoroTheme {
        ChartYearMonth()
    }
}