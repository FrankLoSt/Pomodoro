package com.example.pomodoro


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.MonsterUIState
import com.example.pomodoro.ui.SessionConfig
import com.example.pomodoro.ui.SheetControl
import com.example.pomodoro.ui.countdown.AppPhase
import com.example.pomodoro.ui.countdown.CountDownScreen
import com.example.pomodoro.ui.countdown.FocusUiState
import com.example.pomodoro.ui.countdown.RestUiState
import com.example.pomodoro.ui.countdown.ViewModelCountDown
import com.example.pomodoro.ui.pickmonster.MonsterState
import com.example.pomodoro.ui.pickmonster.MonsterViewModel
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.PickMonsterScreen
import com.example.pomodoro.ui.statistics.PortraitStatisticsScreen
import com.example.pomodoro.ui.statistics.ViewModelChart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val navHostController = rememberNavController()
                val viewModelChart: ViewModelChart = hiltViewModel()
                val monsterViewModel: MonsterViewModel = hiltViewModel()

                ScreenNavigation(
                    navHostController = navHostController,
                    monsterViewModel = monsterViewModel,
                    windowSizeClass = windowSizeClass,
                    viewModelChart = viewModelChart
                )
            }
        }
    }
}









@Composable
fun ScreenNavigation (
    navHostController: NavHostController,
    viewModel: ViewModelCountDown = hiltViewModel(),
    viewModelChart: ViewModelChart,
    focusUiState: FocusUiState = viewModel.focusUiState.collectAsState().value,
    restUiState: RestUiState = viewModel.restUiState.collectAsState().value,
    monsterViewModel: MonsterViewModel = hiltViewModel(),
    monsterState: MonsterState = monsterViewModel.monsterState.collectAsState().value,
    windowSizeClass: WindowSizeClass,
    ) {
    NavHost(
        navController = navHostController,
        startDestination = EnumScreenClass.PICKMONSTER.name
    ){
        composable(EnumScreenClass.PICKMONSTER.name) {
            PickMonsterScreen(
                monsterUI = MonsterUIState(
                    state = monsterState,
                    onMonsterPicked = { monsterViewModel.updateMonsterPickedIndex(it) }
                ),
                sessionConfig = SessionConfig(
                    setDurationMinutes = { viewModel.setDurationMinutes(it) },
                    setRestDurationMinutes = { viewModel.setRestDurationMinutes(it) },
                    setSessions = { viewModel.setSessions(it) },
                    setLongBreakMinutes = { viewModel.setLongBreakMinutes(it) },
                    setLongBreakAfter = { viewModel.setLongBreakAfter(it) },
                    toggleLongBreak = { viewModel.toggleLongBreak(it) },
                    listFocusDuration = focusUiState.listFocusDuration,
                    listRestDuration = restUiState.listRestDuration,
                    listSessions = focusUiState.listSessions,
                    isLongBreak = restUiState.isLongBreak,
                ),
                sheetControl = SheetControl(
                    toggleSetUpPopup = { monsterViewModel.toggleSetUpPopup() },
                    confirmBut = {
                        viewModel.startCountDown()
                        navHostController.navigate(EnumScreenClass.COUNTDOWN.name)
                        monsterViewModel.toggleSetUpPopup()

                    }
                ),
                windowSizeClass = windowSizeClass,
                navHostController = navHostController,
                viewModelChart = viewModelChart
            )
        }
        composable(EnumScreenClass.STATISTICS.name) {
            PortraitStatisticsScreen(
                viewModelChart = viewModelChart,
                navHostController = navHostController,
                monsterViewModel = monsterViewModel
            )
        }
        composable(EnumScreenClass.COUNTDOWN.name) {
            CountDownScreen(
                focusUiState = focusUiState,
                restUiState = restUiState,
                onDismiss = { viewModel.toggleisFinished(); navHostController.navigate(EnumScreenClass.PICKMONSTER.name) },
                breakFun = { viewModel.breakFun() },
                togglePauseResume = { viewModel.togglePauseResume() },
                breakFunDialog = { viewModel.breakFunDialog() },
                windowSizeClass = windowSizeClass,
                monsterId = monsterState.monsterPickedIndex,
                monsterList = monsterState.monsterList,
                onNavigate = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) },
                countDownText = viewModel.formatter(
                    if(focusUiState.appPhrase == AppPhase.FOCUSING)
                        focusUiState.duration
                    else if (focusUiState.sessions == restUiState.longBreakAfter && restUiState.isLongBreak && focusUiState.appPhrase == AppPhase.RESTING)
                        restUiState.longBreakDuration
                    else if ( focusUiState.sessions != restUiState.longBreakAfter && focusUiState.appPhrase == AppPhase.RESTING )
                        restUiState.restDuration
                    else
                            (focusUiState.initialDuration)
                )
            )
        }
    }
}
