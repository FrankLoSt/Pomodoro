package com.example.pomodoro.ui.screen2


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pomodoro.ChartDayHour
import com.example.pomodoro.ChartMonthDay
import com.example.pomodoro.ChartWeekDay
import com.example.pomodoro.ChartYearMonth
import com.example.pomodoro.R
import com.example.pomodoro.data.datastore.ChartUpdate
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.theme.PomodoroTheme
import java.time.format.DateTimeFormatter


@Preview
@Composable
fun PreviewScreen2() {
    PomodoroTheme {
        Screen2LineChart(
            viewModelChart = hiltViewModel(),
            navHostController = NavHostController(LocalContext.current)
        )
    }
}


@Composable
fun Screen2LineChart(
    viewModelChart: ViewModelChart,
    navHostController: NavHostController
) {
    val lastDayActive: String? by viewModelChart.lastDayActive.collectAsState() //String?
    val chartUpdate: ChartUpdate by viewModelChart.chartUpdate.collectAsState()

    val parsedDate: String? = lastDayActive
//to convert from string with custom format to LocalDate or LocalDateTime Object,
// you need to make sure the format used to transform them match the current format of the string, or else -> crash
    val availableDaysList = chartUpdate.availableDays
    val availableWeeksList = chartUpdate.availableWeeks
    val availableMonthsList = chartUpdate.availableMonths
    val availableYearsList = chartUpdate.availableYears

    var colorDay by remember { mutableStateOf(Color.LightGray) }
    var colorWeek by remember { mutableStateOf(Color.LightGray) }
    var colorMonth by remember { mutableStateOf(Color.LightGray) }
    var colorYear by remember { mutableStateOf(Color.LightGray) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if(parsedDate != null )"Last time fighting: $parsedDate" else "No data recorded"
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    viewModelChart.generateChart(viewMode = ViewMode.Day)
                },
                colors = ButtonDefaults.buttonColors(colorDay)
            ) { Text("Day") }
            Button(
                onClick = {
                    viewModelChart.generateChart(viewMode = ViewMode.Week)
                },
                colors = ButtonDefaults.buttonColors(colorWeek)
            ) { Text("Week") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Month)

                },
                colors = ButtonDefaults.buttonColors(colorMonth)
            ) { Text("Month") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Year)
                },
                colors = ButtonDefaults.buttonColors(colorYear)
            ) { Text("Year") }

        } //ViewMode




        when (chartUpdate.viewMode) {
            ViewMode.Month -> {
                colorMonth = Color.Red
                colorDay = Color.LightGray
                colorWeek = Color.LightGray
                colorYear = Color.LightGray


                ButtonViewChart(
                    onClickLeft = {viewMode, leftOrRight ->  viewModelChart.pickDay(viewMode = chartUpdate.viewMode, false) } ,
                    availableList = availableMonthsList,
                    index = chartUpdate.monthIndex,
                    onClickRight = {viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, true)},
                    viewMode = ViewMode.Month,
                    chartUpdate = chartUpdate
                )
                ChartMonthDay(pointsData = chartUpdate.monthDayDataPoints)

            }
            ViewMode.Week -> {
                colorWeek = Color.Red
                colorDay = Color.LightGray
                colorMonth = Color.LightGray
                colorYear = Color.LightGray

                ButtonViewChart(
                    onClickLeft = { viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, false) },
                    availableList = availableWeeksList,
                    index = chartUpdate.weekIndex,
                    onClickRight = {viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, true)},
                    viewMode = ViewMode.Week,
                    chartUpdate = chartUpdate
                )
                ChartWeekDay(pointsData = chartUpdate.weekDayDataPoints)
            }
            ViewMode.Day -> {
                colorDay = Color.Red
                colorWeek = Color.LightGray
                colorMonth = Color.LightGray
                colorYear = Color.LightGray

                ButtonViewChart(
                    onClickLeft = { viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, false) },
                    onClickRight = {viewMode, leftOrRight ->  viewModelChart.pickDay(viewMode = chartUpdate.viewMode, true)},
                    availableList = availableDaysList,
                    index = chartUpdate.dayIndex,
                    viewMode = ViewMode.Day,
                    chartUpdate = chartUpdate
                )
                ChartDayHour(pointsData = chartUpdate.dateHourDataPoint)
            }
            else -> {
                colorYear = Color.Red
                colorWeek = Color.LightGray
                colorMonth = Color.LightGray
                colorDay = Color.LightGray

                ButtonViewChart(
                    onClickLeft = { viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, false) },
                    onClickRight = {viewMode, leftOrRight -> viewModelChart.pickDay(viewMode = chartUpdate.viewMode, true)},
                    availableList = availableYearsList,
                    index = chartUpdate.yearIndex,
                    viewMode = ViewMode.Year,
                    chartUpdate = chartUpdate
                )
                ChartYearMonth(pointsData = chartUpdate.yearMonthDataPoints)
            }
        }

        Button(
            onClick = { navHostController.navigate(EnumScreenClass.screen1.name) }
        ) {
            Text("Back")
        }
    }
}







@Composable
fun ButtonViewChart (
    onClickLeft: (viewMode: ViewMode, leftOrRight: Boolean) -> Unit,
    onClickRight: ( viewMode: ViewMode, leftOrRight: Boolean) -> Unit,
    viewMode: ViewMode,
    availableList: List<Any>,
    chartUpdate: ChartUpdate,
    index: Int = 0,
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onClickLeft(ViewMode.Day, false)},
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.left_navigate),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        if(viewMode != ViewMode.Week) {
            Text(
                text = "${availableList.getOrNull(index) ?: "No Data"}",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Week: ${availableList.getOrNull(index) ?: "No Data"} ",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${chartUpdate.startAndEndWeek.getOrNull(index)?.first?.format(formatter) ?: "No Data"} - ${
                        chartUpdate.startAndEndWeek.getOrNull(
                            index
                        )?.second?.format(formatter) ?: "No Data"
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = { onClickRight(ViewMode.Day, true) },
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.right_navigate),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}









@Composable
fun DropdownFunDay (
    itemLists: List<String>?,
    onItemSelected: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var day by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "${day?.substring(0, 5)}",
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
fun DropdownFunWeek (
    itemLists: List<Int>?,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var week by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "$week",
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
            itemLists?.forEach { weekNum ->
                DropdownMenuItem(
                    text = {
                        Text(text = "$weekNum")
                    },
                    onClick = {
                        onItemSelected(weekNum)
                        week = weekNum
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownFunYear (
    itemLists: List<Int>?,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var year by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "$year",
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
                        Text(text = "$year")
                    },
                    onClick = {
                        onItemSelected(date)
                        year = date
                        expanded = false
                    }
                )
            }?: Text(text = "No Data")
        }
    }
}




@Composable
fun DropdownFunMonth (
    itemLists: List<Int>?,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var month by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "$month",
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
                        Text(text = "$date")
                    },
                    onClick = {
                        onItemSelected(date)
                        month = date
                        expanded = false
                    }
                )
            }?: Text(text = "No Data")
        }
    }
}

@Composable
fun DropdownFunViewMode (
    itemLists: List<ViewMode>?,
    onItemSelected: (ViewMode) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var viewMode1 by rememberSaveable { mutableStateOf(itemLists?.get(0)) }

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
                text = "ViewMode: $viewMode1",
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
            itemLists?.forEach { viewMode ->
                DropdownMenuItem(
                    text = {
                        Text(text = "$viewMode")
                    },
                    onClick = {
                        onItemSelected(viewMode)
                        Log.d("DEBUG", "DropdownFunViewMode: $viewMode")
                        viewMode1 = viewMode
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

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
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
                    val scrollProgress =
                        (firstVisibleIndex.toFloat() / maxScrollIndex).coerceIn(0f, 1f)

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




