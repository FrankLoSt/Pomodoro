package com.example.pomodoro


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
import com.example.pomodoro.ui.Screen1.AlertDialog1
import com.example.pomodoro.ui.Screen1.BreakPauseButtons
import com.example.pomodoro.ui.Screen1.CircularProgressBar
import com.example.pomodoro.ui.Screen1.CountDownButton
import com.example.pomodoro.ui.Screen1.DropDown
import com.example.pomodoro.ui.Screen1.ViewModelCountDown


@Composable
fun Screen1 (
    viewModel: ViewModelCountDown = hiltViewModel(),
) {

    val focusUiState by viewModel.focusUiState.collectAsState()
    val restUiState by viewModel.restUiState.collectAsState()

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
                viewModel = viewModel,
            )
        }

        if(restUiState.isShowingMenu) {
            DropDown(
                focusUiState = focusUiState,
                restUiState = restUiState,
                viewModel = viewModel,
            )
        }
        if(focusUiState.isFinished) {
            AlertDialog1(
                onDismiss = { viewModel.toggleisFinished() },
                focusUiState = focusUiState,
            )
        }
        if(restUiState.isShowingMenu) {
            CountDownButton(viewModel = viewModel)
        } else {
            BreakPauseButtons(
                viewModel = viewModel,
                focusUiState = focusUiState,
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun CountDownTimerPreview () {
    PomodoroTheme {
        Screen1()
    }
}

