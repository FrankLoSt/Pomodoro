package com.example.pomodoro.ui.statistics


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pomodoro.R
import com.example.pomodoro.data.datastore.ChartUpdate
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import java.time.format.DateTimeFormatter


@Composable
fun PortraitStatisticsScreen (
    viewModelChart: ViewModelChart,
    navHostController: NavHostController
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val maxHeight: Dp = maxHeight

        val base = maxOf(maxWidth, maxHeight)

        Column() {
            PortraitInforNaviCard(base)
            LineChartScreen(
                viewModelChart = viewModelChart,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun PortraitInforNaviCard (
    base: Dp
) {
    Card(
        modifier = Modifier.padding(16.dp)
            .fillMaxWidth()
            .height(base * 0.25f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            Text("This is the Infor card")

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /*TODO*/ }
                ){
                    Text("Shop")
                }
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.storepixel),
                        contentDescription = null,
                        tint = Color.Unspecified // disables tinting
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.trophy),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                Button(
                    onClick = { /*TODO*/ }
                ){
                    Text("Settings")
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
)
@Composable
fun PreviewInfo (){
    MyAppTheme {
        PortraitInforNaviCard(base = 400.dp)
    }
}


@Composable
fun LineChartScreen(
    viewModelChart: ViewModelChart,
    navHostController: NavHostController,

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
            .padding(16.dp)
            .verticalScroll(
                rememberScrollState()
            ),
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
                colors = ButtonDefaults.buttonColors(colorDay),

            ) { Text("Day") }
            Button(
                onClick = {
                    viewModelChart.generateChart(viewMode = ViewMode.Week)
                },
                colors = ButtonDefaults.buttonColors(colorWeek),

            ) { Text("Week") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Month)
                },
                colors = ButtonDefaults.buttonColors(colorMonth),

            ) { Text("Month") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Year)
                },
                colors = ButtonDefaults.buttonColors(colorYear),

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
            ViewMode.Year -> {
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
            onClick = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) }
        ) {
            Text("Back")
        }
    }
}







@Composable
fun LandscapeLineChartScreen(
    viewModelChart: ViewModelChart,
    navHostController: NavHostController,
    maxWidth: Dp
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
            .width(maxWidth * 0.7f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if(parsedDate != null )"Last time fighting: $parsedDate" else "No data recorded")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    viewModelChart.generateChart(viewMode = ViewMode.Day)
                },
                colors = ButtonDefaults.buttonColors(colorDay),

                ) { Text("Day") }
            Button(
                onClick = {
                    viewModelChart.generateChart(viewMode = ViewMode.Week)
                },
                colors = ButtonDefaults.buttonColors(colorWeek),

                ) { Text("Week") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Month)
                },
                colors = ButtonDefaults.buttonColors(colorMonth),

                ) { Text("Month") }
            Button(
                onClick = { viewModelChart.generateChart(viewMode = ViewMode.Year)
                },
                colors = ButtonDefaults.buttonColors(colorYear),

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
            ViewMode.Year -> {
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
            onClick = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) }
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































