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
import java.time.LocalDate
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
    val chartState by viewModelChart.chartState.collectAsState()
    val lastDayActive by viewModelChart.lastDayActive.collectAsState() //String?
    val formatterUI = DateTimeFormatter.ofPattern("dd MM yyyy")
    val storageFormatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
    val parsedDate = lastDayActive
        ?.let { LocalDate.parse(it, storageFormatter) }
        ?.format(formatterUI)
//to convert from string with custom format to LocalDate or LocalDateTime Object,
// you need to make sure the format used to transform them match the current format of the string, or else -> crash


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Last time fighting: $parsedDate"
        )
        ChartTest(
            listData = chartState.chartDataYearWeeks
        )
        Button(
            onClick = { navHostController.navigate(EnumScreenClass.screen1.name) }
        ) {
            Text("Back")
        }
    }
}







@Composable
fun ChartTest (
    listData: List<DataPoint>
) {
    LineGraph(
        plot = LinePlot(
            lines = listOf(
                LinePlot.Line(
                    dataPoints = listData,
                    connection = LinePlot.Connection(color = Color.Blue),
                    intersection = LinePlot.Intersection(color = Color.Magenta),
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

    }
}