package com.example.pomodoro


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.pomodoro.ui.components.AlertDialog1
import com.example.pomodoro.ui.components.BreakPauseButtons
import com.example.pomodoro.ui.components.CircularProgressBar
import com.example.pomodoro.ui.components.CountDownButton
import com.example.pomodoro.ui.components.DropDown



import com.example.pomodoro.ui.components.ViewModelCountDown
import com.example.pomodoro.ui.theme.PomodoroTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // ✅ correct



@Composable
fun CountDownTimer (
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
        CountDownTimer()
    }
}

