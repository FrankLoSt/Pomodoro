package com.example.pomodoro.ui.Screen1


import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.navigation.NavHostController
import com.example.pomodoro.data.PomodoroDefaults
import com.example.pomodoro.data.PomodoroPhase
import com.example.pomodoro.data.TimerStatus


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen1 (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    setDurationMinutes : (Int) -> Unit = {},
    setRestDurationMinutes : (Int) -> Unit = {},
    setSessions : (Int) -> Unit = {},
    formatter: (Int) ->  String = { minutes -> "$minutes min"},
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
        if(focusUiState.focusPhase == PomodoroPhase.FOCUS || focusUiState.focusPhase == PomodoroPhase.REST || restUiState.restTimerStatus == TimerStatus.PAUSED || focusUiState.focusTimerStatus == TimerStatus.PAUSED) {
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
                    restUiState = restUiState
                )
            }
        }

        if( (restUiState.restPhase == PomodoroPhase.IDLE && focusUiState.focusPhase == PomodoroPhase.IDLE) && (focusUiState.focusTimerStatus != TimerStatus.RUNNING && restUiState.restTimerStatus != TimerStatus.RUNNING) ) {
            DropDown(
                listSessions = focusUiState.availableSessions,
                listFocusDuration = focusUiState.availableDurations,
                listRestDuration = restUiState.availableDurations,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
            )
            Log.d("DEBUG", "Screen1:  \n focus timer status: ${focusUiState.focusTimerStatus} and rest timer status: ${restUiState.restTimerStatus} \n focus phase: ${focusUiState.focusPhase} and rest phase: ${restUiState.restPhase}")
            CountDownButton(
                startCountDown = startCountDown,
            )
        }

        if( focusUiState.focusPhase == PomodoroPhase.FINISHED ) {
            var toggleDialog by rememberSaveable { mutableStateOf(false) }
            AlertDialog1(
                onDismiss = { toggleDialog = !toggleDialog },
                duration = focusUiState.duration
            )
        }

    }
}



