package com.example.pomodoro.ui.statistics




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pomodoro.R
import com.example.pomodoro.data.datastore.ChartUpdate
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.pickmonster.MonsterViewModel
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import java.time.format.DateTimeFormatter


@Composable
fun PortraitStatisticsScreen (
    viewModelChart: ViewModelChart,
    navHostController: NavHostController,
    monsterViewModel: MonsterViewModel
) {
    val monsterState = monsterViewModel.monsterState.collectAsState()

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
                IconButton (
                    onClick = {navHostController.navigate(EnumScreenClass.PICKMONSTER.name)},
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
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
        BarChartScreen(
            viewModelChart = viewModelChart,
        )
        MonsterLeaderboard2(monsterState.value.top10)
    }
}




@Composable
fun BarChartScreen(
    viewModelChart: ViewModelChart,
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


    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // make the card white
        elevation = CardDefaults.cardElevation(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (parsedDate != null) "Last time fighting: $parsedDate" else "No data recorded"
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
                    onClick = {
                        viewModelChart.generateChart(viewMode = ViewMode.Month)
                    },
                    colors = ButtonDefaults.buttonColors(colorMonth),

                    ) { Text("Month") }
                Button(
                    onClick = {
                        viewModelChart.generateChart(viewMode = ViewMode.Year)
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
                        onClickLeft = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                false
                            )
                        },
                        availableList = availableMonthsList,
                        index = chartUpdate.monthIndex,
                        onClickRight = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                true
                            )
                        },
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
                        onClickLeft = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                false
                            )
                        },
                        availableList = availableWeeksList,
                        index = chartUpdate.weekIndex,
                        onClickRight = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                true
                            )
                        },
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
                        onClickLeft = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                false
                            )
                        },
                        onClickRight = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                true
                            )
                        },
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
                        onClickLeft = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                false
                            )
                        },
                        onClickRight = { viewMode, leftOrRight ->
                            viewModelChart.pickDay(
                                viewMode = chartUpdate.viewMode,
                                true
                            )
                        },
                        availableList = availableYearsList,
                        index = chartUpdate.yearIndex,
                        viewMode = ViewMode.Year,
                        chartUpdate = chartUpdate
                    )
                    BarChartYearMonth(pointsData = chartUpdate.yearMonthDataPoints)
                }
            }
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



data class TopMonsterData (
    val monsterName: String,
    val totalTime: Int,
    val totalSessions: Int,
    val totalWins: Int,
    val totalLoses: Int
) {
    val winRate: Float
        get() = if (totalSessions == 0) 0f else totalWins * 100f / totalSessions
}

@Composable
fun MonsterLeaderboard2(monsters: List<TopMonsterData>) {
    // shared horizontal scroll for header and all rows
    val horizontalScrollState = rememberScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ---------- 1) Fixed HEADER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F6F6))
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // left header (fixed width)
                Row(modifier = Modifier.width(150.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("#", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Monster", modifier = Modifier.width(110.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
                }

                // right header (scrollable horizontally)
                Row(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState) //use HorizontalScroll with shared scrollState for horizontalScroll / Great IDEA
                        .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // column headers - use SAME widths as rows below
                    Text("Time", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Sessions", modifier = Modifier.width(90.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Wins", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Loses", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Win Rate", modifier = Modifier.width(90.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    // add extra columns here if needed (same widths used in rows)
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            // ---------- 2) Body: single LazyColumn (vertical scroll) ----------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // adjust as needed
            ) {  //use LazyColumn for verticalScroll
                itemsIndexed(monsters) { index, monster ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // LEFT FIXED COLUMNS (scroll vertically WITH the list)
                        Row(modifier = Modifier.width(150.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}", modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                monster.monsterName,
                                modifier = Modifier.width(110.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start
                            )
                        }

                        // RIGHT COLUMNS (horizontal scroll synchronized with header)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(horizontalScrollState)
                                .padding(start = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Use same width values as header
                            Text("${monster.totalTime} M", modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                            Text(monster.totalSessions.toString(), modifier = Modifier.width(90.dp), textAlign = TextAlign.Center)
                            Text(monster.totalWins.toString(), modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                            Text(monster.totalLoses.toString(), modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                            Text( String.format("%.1f%%", monster.winRate), modifier = Modifier.width(90.dp), textAlign = TextAlign.Center,
                                color = if (monster.winRate >= 75) Color(0xFF388E3C) else Color(0xFFF57C00))
                            // match any additional columns as necessary
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}


