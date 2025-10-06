package com.example.pomodoro.ui.Screen1


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

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass // ✅
import androidx.compose.ui.unit.DpSize
import androidx.navigation.NavHostController
import com.example.pomodoro.data.AppPhase
import com.example.pomodoro.data.TimerState


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen1 (
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
    breakFunDialog: () -> Unit,
    navHostController: NavHostController
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
    ) {
        //session title

        if(focusUiState.appPhrase == AppPhase.IDLE  && focusUiState.timerState == TimerState.STOPPED) {
            DropDown(
                listSessions = focusUiState.listSessions,
                listFocusDuration = focusUiState.listFocusDuration,
                listRestDuration = restUiState.listRestDuration,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
            )
            CountDownButton(
                startCountDown = startCountDown,
            )
        }
        else  {
            CircularProgressBar(
                focusUiState = focusUiState,
                restUiState = restUiState,
                formatter = formatter
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreakButton(
                    breakFun = breakFun,
                    breakFunDialog = breakFunDialog
                )
                PauseButton(
                    togglePauseResume = togglePauseResume,
                    focusUiState = focusUiState,
                )
            }
        }

        if(focusUiState.appPhrase == AppPhase.FINISHED) {
            AlertDialog1(
                onDismiss = toggleisFinished,
                duration = focusUiState.duration
            )
        }

    }
}



