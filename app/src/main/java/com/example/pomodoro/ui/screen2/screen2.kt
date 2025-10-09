package com.example.pomodoro.ui.screen2

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pomodoro.data.datastore.ChartState
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
import com.example.pomodoro.data.datastore.ChartUpdate


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
    val chartState: ChartState by viewModelChart.chartState.collectAsState()
    val lastDayActive: String? by viewModelChart.lastDayActive.collectAsState() //String?
    val chartUpdate: ChartUpdate by viewModelChart.chartUpdate.collectAsState()

    val parsedDate: String? = lastDayActive
//to convert from string with custom format to LocalDate or LocalDateTime Object,
// you need to make sure the format used to transform them match the current format of the string, or else -> crash
    val availableDaysList = chartUpdate.availableDays
    Log.d("DEBUG", "Screen2LineChart: $availableDaysList")

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if(parsedDate != null )"Last time fighting: $parsedDate" else "No data recorded"
        )
        DropdownFun(
            itemLists = availableDaysList,
            onItemSelected = { viewModelChart.pickDay(it) }
        )

        ChartTest(
            listData = chartUpdate.dateHourDataPoint
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

@Preview
@Composable
fun Scrollable () {
    ScrollableDropdownMenuDemo()
}


@Composable
fun DropdownFun (
    itemLists: List<String>?,
    onItemSelected: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var day by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .width(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "Date: $day",
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp) // limit height
        ) {
            itemLists?.forEach { date ->
                DropdownMenuItem(
                    text = {
                        Text(text = date)
                    },
                    onClick = {
                        onItemSelected(date)
                        day = date
                        expanded = false
                    }
                )
            }?: Text(text = "No Data")
        }
    }
}


@Composable
fun ScrollableDropdownMenuDemo() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<String?>(null) }

    val items = List(50) { "Item ${it + 1}" } // Simulate a long list
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = selectedItem ?: "Select an item",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Dropdown") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .height(200.dp) // Limit height to enable scrolling
                .fillMaxWidth()
        ) {
            LazyColumn(state = listState) {
                items(items) { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            selectedItem = item
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollableDropdownMenuWithScrollbar() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<String?>(null) }

    val items = List(50) { "Item ${it + 1}" }
    val listState = rememberLazyListState()
    val dropdownHeight = 200.dp

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        var textFieldHeight by remember { mutableStateOf(0) }

        Column {
            OutlinedTextField(
                value = selectedItem ?: "Select an item",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textFieldHeight = it.size.height
                    }
                    .clickable { expanded = !expanded },
                label = { Text("Dropdown") },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            )

            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dropdownHeight)
                        .offset(y = with(LocalDensity.current) { textFieldHeight.toDp() })
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 8.dp)
                    ) {
                        items(items) { item ->
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = item
                                        expanded = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }

                    // Scrollbar
                    val totalItems = items.size
                    val visibleItems = listState.layoutInfo.visibleItemsInfo.size
                    val firstVisibleIndex = listState.firstVisibleItemIndex
                    val maxScrollIndex = (totalItems - visibleItems).coerceAtLeast(1)
                    val scrollProgress = (firstVisibleIndex.toFloat() / maxScrollIndex).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(4.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight(fraction = 0.2f)
                            .offset(y = scrollProgress * dropdownHeight)
                            .width(4.dp)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}




