package com.example.pomodoro.ui.Screen1

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.theme.PomodoroTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdaptiveCountdownScreen (
    windowSize: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp)),
    windowWidth: WindowWidthSizeClass = windowSize.widthSizeClass,
    windowHeight: WindowHeightSizeClass = windowSize.heightSizeClass,
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    setDurationMinutes : (Int) -> Unit = {},
    setRestDurationMinutes : (Int) -> Unit = {},
    setSessions : (Int) -> Unit = {},
    formatter: (Int) ->  String = { minutes -> "$minutes min"},
    toggleisFinished: () -> Unit,
    startCountDown: () -> Unit,
    breakFun: () -> Unit,
    togglePauseResume: () -> Unit,
) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Screen1(
                focusUiState = focusUiState,
                restUiState = restUiState,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
                formatter = formatter,
                toggleisFinished = toggleisFinished,
                startCountDown = startCountDown,
                breakFun = breakFun,
                togglePauseResume = togglePauseResume,
            )
        }
        WindowWidthSizeClass.Medium -> {
            ExpandedScreen(
                focusUiState = focusUiState,
                restUiState = restUiState,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
                startCountDown = startCountDown,
                breakFun = breakFun,
                togglePauseResume = togglePauseResume,
                toggleisFinished = toggleisFinished,
                formatter = formatter
            )
        }
        WindowWidthSizeClass.Expanded -> {
            Screen1(
                focusUiState = focusUiState,
                restUiState = restUiState,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
                formatter = formatter,
                toggleisFinished = toggleisFinished,
                startCountDown = startCountDown,
                breakFun = breakFun,
                togglePauseResume = togglePauseResume,
            )
        }
    }
}






@RequiresApi(Build.VERSION_CODES.O)
@Preview (showBackground = true)
@Composable
fun CountDownTimerPreview () {
    PomodoroTheme {
        AdaptiveCountdownScreen(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            setDurationMinutes = {},
            setRestDurationMinutes = {},
            setSessions = {},
            formatter = { minutes -> "$minutes min" },
            toggleisFinished = {},
            startCountDown = {},
            breakFun = {},
            togglePauseResume = {},
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview (
    name = "Expanded Landscape",
    widthDp = 800,
    heightDp = 400,
    showBackground = true
)
@Composable
fun CountDownTimerPreviewExpanded () {
    PomodoroTheme {
        AdaptiveCountdownScreen(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            setDurationMinutes = {},
            setRestDurationMinutes = {},
            setSessions = {},
            formatter = { minutes -> "$minutes min" },
            toggleisFinished = {},
            startCountDown = {},
            breakFun = {},
            togglePauseResume = {},
        )
    }
}
