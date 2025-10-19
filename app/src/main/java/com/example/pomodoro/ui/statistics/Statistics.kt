package com.example.pomodoro.ui.statistics




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    Column(
        modifier = Modifier.fillMaxSize()
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
            ) {
                Text("This is the Infor card")

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.monster1),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(base * 0.1f)
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.treemonster),
                            contentDescription = null,
                            tint = Color.Unspecified // disables tinting
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.spider),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.warrior2),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
        val barData = listOf(
            BarData(Point(0f, 4f), Color(0xFF4CAF50), "Mon"),
            BarData(Point(1f, 6f), Color(0xFF2196F3), "Tue"),
            BarData(Point(2f, 3.5f), Color(0xFFFFC107), "Wed"),
            BarData(Point(3f, 7f), Color(0xFFF44336), "Thu"),
            BarData(Point(4f, 5f), Color(0xFF9C27B0), "Fri")
        )

        // 🔹 X-Axis Configuration
        val xAxisData = AxisData.Builder()
            .axisStepSize(60.dp)
            .steps(barData.size - 1)
            .labelData { i -> barData[i].label }
            .axisLabelFontSize(14.sp)
            .axisLabelColor(Color(0xFF616161))
            .axisLineColor(Color.Transparent)
            .axisLabelAngle(0f)
            .startDrawPadding(20.dp) // 👈 add initial offset for the first bar
            .build()

        // 🔹 Y-Axis Configuration
        val yAxisData = AxisData.Builder()
            .steps(6)
            .labelData { i -> (i * 2).toString() }
            .axisLineColor(Color(0xFFE0E0E0))
            .axisLabelColor(Color(0xFF757575))
            .axisLabelFontSize(12.sp)
            .axisLabelAngle(0f)
            .axisStepSize(40.dp)
            .build()

        // 🔹 Bar Styling
        val barStyle = BarStyle(
            barWidth = 36.dp,                        // slightly wider for better spacing
            cornerRadius = 10.dp,                    // smoother edges for a modern look
            paddingBetweenBars = 20.dp,              // consistent breathing room
            isGradientEnabled = true,                // enables gradient rendering
            barBlendMode = BlendMode.SrcOver,        // softer blending
            // solid fill
            selectionHighlightData = SelectionHighlightData( // highlight when tapped
                isHighlightBarRequired = true,
                highlightBarColor = Color(0xFF4CAF50),
                highlightBarStrokeWidth = 12.dp,
                highlightBarCornerRadius = 10.dp
            ),

        )


        // 🔹 Bar Chart Data
        val barChartData = BarChartData(
            chartData = barData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = Color(0xFFF5F5F5),
            horizontalExtraSpace = 40.dp,
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
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    BarChart(
                        modifier = Modifier.fillMaxSize(),
                        barChartData = barChartData
                    )
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
data class ChartDataPoint(
    val value: Float,
    val description: String,
    val color: Color
)

data class DataCategoryOption(
    val name: String,
    val color: Color
)





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































