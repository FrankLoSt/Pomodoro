package com.example.pomodoro.ui.screen2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen2LineChart2(
    viewModel: FocusChartViewModel,
    navHostController: NavHostController
) {
    val chartState by viewModel.chartState.collectAsState()
    val lastDayActive by viewModel.lastActiveDay.collectAsState() //String?
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
        ChartScreen(
            viewModel = viewModel,
            navHostController =  navHostController
        )
        Button(
            onClick = { navHostController.navigate(EnumScreenClass.screen1.name) }
        ) {
            Text("Back")
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChartScreen(
    viewModel: FocusChartViewModel,
    navHostController: NavHostController
) {
    val chartState by viewModel.chartState.collectAsState()
    val lastActive by viewModel.lastActiveDay.collectAsState()

    // Example: Load when screen starts
    LaunchedEffect(Unit) {
        viewModel.loadChart(ViewMode.WeekDay) // default mode
    }

    Column {
        Text("Last active: ${lastActive ?: "N/A"}")
        ChartTest(listData = chartState.chartDataDay)
    }
}
