package com.example.pomodoro.ui.screen2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.google.apps.card.v1.Button
import com.google.apps.card.v1.Columns
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun Screen2Test(
    text: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen2LineChart(
    viewModelChart: ViewModelChart,
    navHostController: NavHostController
) {
    val lastDayActive by viewModelChart.lastDayActive.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Last time fighting: ${lastDayActive ?: "No fight recorded"}"
        )
        ChartTest()
        Button(
            onClick = { navHostController.navigate(EnumScreenClass.screen1.name) }
        ) {
            Text("Back")
        }
    }
}







@Composable
fun ChartTest (
    listData: List<DataPoint> = listOf(
        DataPoint(x = 0f, y = 0f),
        DataPoint(x = 1f, y = 0f),
        DataPoint(x = 2f, y = 0f),
        DataPoint(x = 3f, y = 0f),
        DataPoint(x = 4f, y = 0f),
        DataPoint(x = 5f, y = 0f),
        DataPoint(x = 6f, y = 600f),    // 10 mins
        DataPoint(x = 7f, y = 1800f),   // 30 mins
        DataPoint(x = 8f, y = 1200f),   // 20 mins
        DataPoint(x = 9f, y = 2400f),   // 40 mins
        DataPoint(x = 10f, y = 3600f),  // 1 hour
        DataPoint(x = 11f, y = 1800f),  // 30 mins
        DataPoint(x = 12f, y = 900f),   // 15 mins
        DataPoint(x = 13f, y = 0f),
        DataPoint(x = 14f, y = 1500f),  // 25 mins
        DataPoint(x = 15f, y = 2700f),  // 45 mins
        DataPoint(x = 16f, y = 0f),
        DataPoint(x = 17f, y = 3000f),  // 50 mins
        DataPoint(x = 18f, y = 600f),   // 10 mins
        DataPoint(x = 19f, y = 1800f),  // 30 mins
        DataPoint(x = 20f, y = 1200f),  // 20 mins
        DataPoint(x = 21f, y = 900f),   // 15 mins
        DataPoint(x = 22f, y = 0f),
        DataPoint(x = 23f, y = 0f)
    )

) {
    LineGraph(
        plot = LinePlot(
            lines = listOf(
                LinePlot.Line(
                    dataPoints = listData,
                    connection = LinePlot.Connection(color = Color.Blue),
                    intersection = LinePlot.Intersection(color = Color.Red),
                    highlight = LinePlot.Highlight(color = Color.Yellow)
                )
            ),
            grid = LinePlot.Grid(Color.LightGray, steps = 6)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onSelection = { xLine, points ->
            // Optional: handle user tap on a point
        }
    )
}



@Preview
@Composable
fun ChartPreview () {
    PomodoroTheme {
        ChartTest()
    }
}