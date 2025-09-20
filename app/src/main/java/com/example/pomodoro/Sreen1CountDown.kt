package com.example.pomodoro


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.Screen1.AlertDialog1

import com.example.pomodoro.ui.Screen1.CircularProgressBar
import com.example.pomodoro.ui.Screen1.CountDownButton
import com.example.pomodoro.ui.Screen1.DropDown
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass // ✅
import androidx.compose.ui.unit.DpSize
import com.example.pomodoro.ui.Screen1.BreakButton
import com.example.pomodoro.ui.Screen1.PauseButton


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdaptiveCountdownScreen (
    windowSize: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp)),
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
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> {
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
    }
}









@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen1 (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    setDurationMinutes : (Int) -> Unit = {},
    setRestDurationMinutes : (Int) -> Unit = {},
    setSessions : (Int) -> Unit = {},
    formatter: (Int) ->  String = { minutes -> "$minutes min"},
    toggleisFinished: () -> Unit ,
    startCountDown: () -> Unit ,
    breakFun: () -> Unit ,
    togglePauseResume: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
    ) {
        //session title
        if(!restUiState.isShowingMenu) {
            CircularProgressBar(
                focusUiState = focusUiState,
                restUiState = restUiState,
                formatter = formatter
            )
        }

        if(restUiState.isShowingMenu) {
            DropDown(
                listSessions = focusUiState.listSessions,
                listFocusDuration = focusUiState.listFocusDuration,
                listRestDuration = restUiState.listRestDuration,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
            )
        }
        if(focusUiState.isFinished) {
            AlertDialog1(
                onDismiss = toggleisFinished,
                duration = focusUiState.duration
            )
        }
        if(restUiState.isShowingMenu) {
            CountDownButton(
                startCountDown = startCountDown,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreakButton(
                    breakFun = breakFun,
                    breakFunDialog = togglePauseResume
                )
                PauseButton(
                    togglePauseResume = togglePauseResume,
                    focusUiState = focusUiState,
                )
            }
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
            formatter = { minutes -> "$minutes min"},
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
            formatter = { minutes -> "$minutes min"},
            toggleisFinished = {},
            startCountDown = {},
            breakFun = {},
            togglePauseResume = {},
        )
    }
}

