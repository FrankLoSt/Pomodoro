package com.example.pomodoro


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.ui.theme.PomodoroTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // ✅ correct
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.Screen1.AlertDialog1
import com.example.pomodoro.ui.Screen1.BreakPauseButtons
import com.example.pomodoro.ui.Screen1.CircularProgressBar
import com.example.pomodoro.ui.Screen1.CountDownButton
import com.example.pomodoro.ui.Screen1.DropDown
import com.example.pomodoro.ui.Screen1.ViewModelCountDown


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen1 (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    setDurationMinutes : (Int) -> Unit = {},
    setRestDurationMinutes : (Int) -> Unit = {},
    setSessions : (Int) -> Unit = {},
    formatter: (Int) ->  String = { minutes -> "$minutes min"},
    toggleisFinished: () -> Unit = {},
    startCountDown: () -> Unit = {},
    breakFun: () -> Unit = {},
    togglePauseResume: () -> Unit = {},
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
                focusUiState = focusUiState,
                restUiState = restUiState,
                setDurationMinutes = setDurationMinutes,
                setRestDurationMinutes = setRestDurationMinutes,
                setSessions = setSessions,
            )
        }
        if(focusUiState.isFinished) {
            AlertDialog1(
                onDismiss = toggleisFinished,
                focusUiState = focusUiState,
            )
        }
        if(restUiState.isShowingMenu) {
            CountDownButton(
                startCountDown = startCountDown,
            )
        } else {
            BreakPauseButtons(
                breakFun = breakFun,
                togglePauseResume = togglePauseResume,
                focusUiState = focusUiState,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview (showBackground = true)
@Composable
fun CountDownTimerPreview () {
    PomodoroTheme {
        Screen1(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
        )
    }
}

