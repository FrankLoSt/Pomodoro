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

@Composable
fun ChartDayHour (
    pointsData: List<Point>
) {
    val steps = 5
    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(14.dp)
        .backgroundColor(Color.Transparent)
        .steps(23)
        .labelData { i ->
            when (i) {
                0 -> "  0"
                6 -> "6"
                12 -> "12"
                18 -> "18"
                23 -> "23"
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
        .labelAndAxisLinePadding(15.dp)
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

@Composable
fun ChartWeekDay (
    pointsData: List<Point>
) {
    val steps = 5
    val pointsData = pointsData

    val xAxisData = AxisData.Builder()
        .axisStepSize(55.dp)
        .backgroundColor(Color.Transparent)
        .steps(7)
        .axisOffset(20.dp)
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