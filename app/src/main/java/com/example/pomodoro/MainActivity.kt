package com.example.pomodoro

import android.os.Bundle
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.Screen1.DashBoard
import com.example.pomodoro.ui.Screen1.MyAppTheme
import com.example.pomodoro.ui.Screen1.Screen1
import com.example.pomodoro.ui.Screen1.ViewModelCountDown
import com.example.pomodoro.ui.screen2.Screen2LineChart
import com.example.pomodoro.ui.screen2.ViewModelChart
import com.example.pomodoro.ui.theme.PomodoroTheme
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
                                        navHostController.navigate(EnumScreenClass.screen2.name)
                                        scope.launch {
                                            drawerState.close()
                                            viewModelChart.generateChart(ViewMode.Day)
                                        }
                                    }
                                )
                            NavigationDrawerItem(
                                label = { Text("Count Down") },
                                selected = false,
                                onClick = {
                                    navHostController.navigate(EnumScreenClass.screen1.name)
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
                                viewModelChart = viewModelChart
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
) {
    NavHost(
        navController = navHostController,
        startDestination = EnumScreenClass.screen1.name
    ){
        composable(EnumScreenClass.screen1.name) {
            DashBoard(
                windowSize = WindowWidthSizeClass.Compact,
                fightToggleDialog = { viewModel.fightToggleDialog() },
                setDurationMinutes = { viewModel.setDurationMinutes(it) },
                setRestDurationMinutes = { viewModel.setRestDurationMinutes(it) },
                setSessions = { viewModel.setSessions(it) },
                listFocusDuration = focusUiState.listFocusDuration,
                listRestDuration = restUiState.listRestDuration,
                listSessions = focusUiState.listSessions,
                confirmBut = { viewModel.startCountDown() }
            )
        }
        composable(EnumScreenClass.screen2.name) {
            Screen2LineChart(
                viewModelChart = viewModelChart,
                navHostController = navHostController
            )
        }
    }
}
