package com.example.pomodoro

import android.os.Bundle
import android.util.Log
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
import com.example.pomodoro.ui.countdown.AppPhase
import com.example.pomodoro.ui.countdown.CountDownScreen
import com.example.pomodoro.ui.countdown.FocusUiState
import com.example.pomodoro.ui.countdown.RestUiState
import com.example.pomodoro.ui.countdown.ViewModelCountDown
import com.example.pomodoro.ui.pickmonster.InitSetUpState
import com.example.pomodoro.ui.pickmonster.MonsterViewModel
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.PickMonsterScreen
import com.example.pomodoro.ui.statistics.LineChartScreen
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

                PortraitStatisticsScreen(
                    viewModelChart = viewModelChart,
                    navHostController = navHostController
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
    initSetUpState: InitSetUpState = monsterViewModel.initSetUpState.collectAsState().value,
    windowSizeClass: WindowSizeClass,
    ) {
    NavHost(
        navController = navHostController,
        startDestination = EnumScreenClass.PICKMONSTER.name
    ){
        composable(EnumScreenClass.PICKMONSTER.name) {
            PickMonsterScreen(
                windowSizeClass = windowSizeClass,
                toggleSetUpPopup = { monsterViewModel.toggleSetUpPopup() },
                setDurationMinutes = { viewModel.setDurationMinutes(it) },
                setRestDurationMinutes = { viewModel.setRestDurationMinutes(it) },
                setSessions = { viewModel.setSessions(it) },
                listFocusDuration = focusUiState.listFocusDuration,
                listRestDuration = restUiState.listRestDuration,
                listSessions = focusUiState.listSessions,
                confirmBut = {
                    viewModel.startCountDown();
                    navHostController.navigate(EnumScreenClass.COUNTDOWN.name);
                    monsterViewModel.toggleSetUpPopup()
                    Log.e("DEBUG", "Monster picked : ${initSetUpState.monsterPickedIndex}")
                             },
                initSetUpState = initSetUpState,
                navHostController = navHostController,
                updateMonsterPickedIndex = { monsterViewModel.updateMonsterPickedIndex(it) },
                viewModelChart = viewModelChart
            )
        }
        composable(EnumScreenClass.STATISTICS.name) {
            LineChartScreen(
                viewModelChart = viewModelChart,
                navHostController = navHostController,
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
                monsterId = initSetUpState.monsterPickedIndex,
                monsterList = initSetUpState.monsterList,
                onNavigate = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) },
                countDownText = viewModel.formatter(if(focusUiState.appPhrase == AppPhase.FOCUSING) focusUiState.duration else if (focusUiState.appPhrase == AppPhase.RESTING) restUiState.restDuration else (focusUiState.initialDuration))
            )
        }
    }
}
