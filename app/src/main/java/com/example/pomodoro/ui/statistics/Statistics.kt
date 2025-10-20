package com.example.pomodoro.ui.statistics




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Fill
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.AccessibilityConfig
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.wavechart.model.AxisPosition
import com.example.pomodoro.R
import com.example.pomodoro.data.datastore.ChartUpdate
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import java.time.format.DateTimeFormatter


@Composable
fun Xbe (
    viewModelChart: ViewModelChart,
    navHostController: NavHostController
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val maxHeight: Dp = maxHeight

        val base = maxOf(maxWidth, maxHeight)

        Column(
            modifier = Modifier.fillMaxSize().scrollable(rememberScrollState(), orientation = Orientation.Vertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

        }
    }
}

@Composable
fun PortraitStatisticsScreen (
    viewModelChart: ViewModelChart,
    navHostController: NavHostController,

) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(
            rememberScrollState()
        )
    ) {
        Card(
            modifier = Modifier.padding(bottom = 16.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){

                Text("This is the Infor card")

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /*TODO*/ },

                    ) { Text("Button1") }
                    Button(
                        onClick = { /*TODO*/ },

                    ) {  Text("Button2")}
                    Button(
                        onClick = { /*TODO*/ },

                    ) { Text("Button3") }
                    Button(
                        onClick = { /*TODO*/ },

                    ) { Text("Button4") }
                }
            }
        }
        LineChartScreen(
            viewModelChart = viewModelChart,
            navHostController = navHostController
        )
    }
}


@Preview(
    showBackground = true,
)
@Composable
fun PreviewInfo (){
    MyAppTheme {

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
                BarChartMonthDay(pointsData = chartUpdate.monthDayDataPoints)

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
                BarChartWeekDay(pointsData = chartUpdate.weekDayDataPoints)
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
                BarChartDayHour(pointsData = chartUpdate.dateHourDataPoint)
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
                BarChartYearMonth(pointsData = chartUpdate.yearMonthDataPoints)
            }
        }

        Button(
            onClick = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) }
        ) { Text("Back") }
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
                BarChartMonthDay(pointsData = chartUpdate.monthDayDataPoints)

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
                BarChartWeekDay(pointsData = chartUpdate.weekDayDataPoints)
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
                BarChartDayHour(pointsData = chartUpdate.dateHourDataPoint)
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































