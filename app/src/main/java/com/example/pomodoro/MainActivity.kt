package com.example.pomodoro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodoro.ui.countdown.FocusUiState
import com.example.pomodoro.ui.countdown.RestUiState
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.countdown.CountDownScreen

import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.PickMonsterScreen
import com.example.pomodoro.ui.countdown.ViewModelCountDown
import com.example.pomodoro.ui.pickmonster.InitSetUpState
import com.example.pomodoro.ui.pickmonster.MonsterViewModel
import com.example.pomodoro.ui.statistics.LineChartScreen
import com.example.pomodoro.ui.statistics.ViewModelChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                val navHostController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val viewModelChart: ViewModelChart = hiltViewModel()

                val windowSize = LocalWindowInfo.current.containerSize
                val density = LocalDensity.current
                val screenWidth = with(density) { windowSize.width.toDp().value }
                val screenHeight = with(density) { windowSize.height.toDp().value }
                val isLandscape = screenWidth > screenHeight

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(200.dp)
                        ) {
                            Row () {
                                Icon(
                                    painter = painterResource(R.drawable.sprite_11_2),
                                    contentDescription = null,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Text(
                                    stringResource(R.string.menu),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.statistics)) },
                                    selected = false,
                                    onClick = {
                                        navHostController.navigate(EnumScreenClass.STATISTICS.name)
                                        scope.launch {
                                            drawerState.close()
                                            viewModelChart.generateChart(ViewMode.Day)
                                        }
                                    }
                                )
                            NavigationDrawerItem(
                                label = { Text("Home screen") },
                                selected = false,
                                onClick = {
                                    navHostController.navigate(EnumScreenClass.PICKMONSTER.name)
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text(stringResource(R.string.about)) },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            if(isLandscape) null else {
                                TopAppBar(
                                    title = { Text(stringResource(R.string.menu)) },
                                    navigationIcon = {
                                        IconButton(
                                            onClick = { scope.launch { drawerState.open() } }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.sprite_11_2),
                                                contentDescription = stringResource(R.string.menu)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            ScreenNavigation(
                                navHostController = navHostController,
                                viewModelChart = viewModelChart,
                                windowSizeClass = calculateWindowSizeClass(this),
                            )
                        }
                    }
                }
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
                monsterViewModel = monsterViewModel,
            )
        }
        composable(EnumScreenClass.STATISTICS.name) {
            LineChartScreen(
                viewModelChart = viewModelChart,
                navHostController = navHostController
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
                navHostController = navHostController,
                monsterId = initSetUpState.monsterPickedIndex,
                monsterList = initSetUpState.monsterList
            )
        }
    }
}
