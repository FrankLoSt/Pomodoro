package com.example.pomodoro

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.Screen1.AdaptiveCountdownScreen
import com.example.pomodoro.ui.Screen1.ViewModelCountDown
import com.example.pomodoro.ui.theme.PomodoroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val width = windowSizeClass.widthSizeClass
            val height = windowSizeClass.heightSizeClass
            PomodoroTheme {
                val viewModel: ViewModelCountDown = hiltViewModel()
                AdaptiveCountdownScreen(
                    focusUiState = viewModel.focusUiState.collectAsState().value,
                    restUiState = viewModel.restUiState.collectAsState().value,
                    setDurationMinutes = { viewModel.setDurationMinutes(it) },
                    setRestDurationMinutes = { viewModel.setRestDurationMinutes(it) },
                    setSessions = { viewModel.setSessions(it) },
                    formatter = { viewModel.formatter(it) },
                    toggleisFinished = { viewModel.toggleisFinished() },
                    startCountDown = { viewModel.startCountDown() },
                    breakFun = { viewModel.breakFun() },
                    togglePauseResume = { viewModel.togglePauseResume() },
                    windowSize = windowSizeClass
                )
            }
        }
    }
}

//haven't done screen 2 yet
@Composable
fun ScreenNavigation (
    navHostController: NavHostController = rememberNavController(),
    viewModel: ViewModelCountDown = hiltViewModel(),
    focusUiState: FocusUiState = viewModel.focusUiState.collectAsState().value,
    restUiState: RestUiState = viewModel.restUiState.collectAsState().value,
) {}
