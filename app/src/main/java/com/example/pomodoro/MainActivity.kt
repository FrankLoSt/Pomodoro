package com.example.pomodoro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.pomodoro.ui.countdown.FocusUiState
import com.example.pomodoro.ui.countdown.RestUiState
import com.example.pomodoro.data.datastore.ViewMode
import com.example.pomodoro.ui.EnumScreenClass
import com.example.pomodoro.ui.ScreenShape
import com.example.pomodoro.ui.countdown.CountDownScreen

import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.PickMonsterScreen
import com.example.pomodoro.ui.countdown.ViewModelCountDown
import com.example.pomodoro.ui.detectScreenShape
import com.example.pomodoro.ui.pickmonster.FontSize
import com.example.pomodoro.ui.pickmonster.InitSetUpState
import com.example.pomodoro.ui.pickmonster.LocalFontSize
import com.example.pomodoro.ui.pickmonster.LocalSpacing
import com.example.pomodoro.ui.pickmonster.MonsterViewModel
import com.example.pomodoro.ui.statistics.LineChartScreen
import com.example.pomodoro.ui.statistics.ViewModelChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

                val windowSizeCheck = LocalWindowInfo.current.containerSize
                val density = LocalDensity.current
                val screenWidth = with(density) { windowSizeCheck.width.toDp() }.value.toInt()
                val screenHeight = with(density) { windowSizeCheck.height.toDp() }.value.toInt()

                val screenShape = detectScreenShape(
                    windowSizeClass.widthSizeClass,
                    windowSizeClass.heightSizeClass,
                    screenWidth,
                    screenHeight
                )
                val scope: CoroutineScope = rememberCoroutineScope()

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val spacing = LocalSpacing.current
                val fontSize: FontSize = LocalFontSize.current


                var showDrawer: Boolean by rememberSaveable { mutableStateOf(false) }

                when (screenShape) {
                    is ScreenShape.PhonePortrait -> Drawer(
                        navHostController = navHostController,
                        viewModelChart = viewModelChart,
                        scope = scope,
                        drawerState = drawerState,
                        drawerContent = {
                            ScreenNavigation(
                                navHostController = navHostController,
                               viewModelChart = viewModelChart,
                               windowSizeClass = windowSizeClass
                            )
                        }
                    )

                    is ScreenShape.PhoneLandscape -> RightSideDrawer(
                        isOpen = showDrawer,
                        onClose = {
                            showDrawer = false
                        },
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.width((screenWidth * 0.2f).dp)
                            ) {
                                Row() {
                                    Icon(
                                        painter = painterResource(R.drawable.sprite_11_2),
                                        contentDescription = "Open Drawer",
                                        modifier = Modifier.size((screenWidth * 0.03f).dp)
                                    )
                                    Text(
                                        stringResource(R.string.menu),
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.statistics)) },
                                    selected = false,
                                    onClick = {
                                        navHostController.navigate(EnumScreenClass.STATISTICS.name)
                                        scope.launch {
                                            showDrawer = false
                                            viewModelChart.generateChart(ViewMode.Day)
                                        }
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text("Home screen") },
                                    selected = false,
                                    onClick = {
                                        navHostController.navigate(EnumScreenClass.PICKMONSTER.name)
                                        showDrawer = false
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.about)) },
                                    selected = false,
                                    onClick = { /*TODO*/ }
                                )
                            }
                        },
                        mainContent = {
                            Box(
                                modifier = Modifier.clickable(
                                    onClick = { showDrawer = false },
                                    indication = null, // 🔥 disables ripple
                                    interactionSource = remember { MutableInteractionSource() } // 🔒 disables press animation
                                )
                            ) {
                                ScreenNavigation(
                                    navHostController = navHostController,
                                    viewModelChart = viewModelChart,
                                    windowSizeClass = windowSizeClass
                                )
                                IconButton(
                                    onClick = { showDrawer = true },
                                    modifier = Modifier.size((screenWidth * 0.15f).dp)
                                        .align(Alignment.TopEnd)
                                        .padding(end = spacing.medium, top = spacing.medium)
                                ) {
                                    Row {
                                        Text(
                                            "Menu",
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(end = spacing.medium)
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.sprite_11_2),
                                            contentDescription = "Open Drawer",
                                            modifier = Modifier.size((screenWidth * 0.03f).dp)
                                        )
                                    }
                                }
                            }
                        }
                    )

                    is ScreenShape.TabletPortrait -> Drawer(
                        navHostController = navHostController,
                        viewModelChart = viewModelChart,
                        scope = scope,
                        drawerState = drawerState,
                        drawerContent = {
                            ScreenNavigation(
                                navHostController = navHostController,
                                viewModelChart = viewModelChart,
                                windowSizeClass = windowSizeClass
                            )
                        }
                    )

                    is ScreenShape.TabletLandscape -> RightSideDrawer(
                        isOpen = false,
                        onClose = {},
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Row() {
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
                        },
                        mainContent = {
                            ScreenNavigation(
                                navHostController = navHostController,
                                viewModelChart = viewModelChart,
                                windowSizeClass = windowSizeClass
                            )
                        }
                    )
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer (
    navHostController: NavHostController,
    viewModelChart: ViewModelChart,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit
    ) {


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
                Row() {
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
                if (isLandscape) null else {
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
                drawerContent()
            }
        }
    }
}

@Composable
fun RightSideDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),

    ) {
        mainContent()

        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            drawerContent()
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
                updateMonsterPickedIndex = { monsterViewModel.updateMonsterPickedIndex(it) }
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
                monsterId = initSetUpState.monsterPickedIndex,
                monsterList = initSetUpState.monsterList,
                onNavigate = { navHostController.navigate(EnumScreenClass.PICKMONSTER.name) }
            )
        }
    }
}
