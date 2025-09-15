package com.example.pomodoro


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.data.PomodoroState
import com.example.pomodoro.ui.components.BreakPauseButtons
import com.example.pomodoro.ui.components.CircularProgressBar
import com.example.pomodoro.ui.components.CountDownButton
import com.example.pomodoro.ui.components.CustomCircularProgressIndicator
import com.example.pomodoro.ui.components.DropDown


import com.example.pomodoro.ui.components.DropdownFun

import com.example.pomodoro.ui.components.ViewModelCountDown

@Composable
fun CountDownTimer(
    viewModel: ViewModelCountDown = viewModel(),
) {
    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val focusUiState by viewModel.focusUiState.collectAsState()
    val restUiState by viewModel.restUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
    ) {

        // --- Circular Slider or Progress Bar ---
        when (pomodoroState) {
            is PomodoroState.Idle -> {
                DropDown(
                    focusUiState = focusUiState,
                    restUiState = restUiState,
                    viewModel = viewModel
                )
                CountDownButton(viewModel = viewModel) // show Start button
            }

            is PomodoroState.Studying -> {
                    CircularProgressBar(
                        focusUiState = focusUiState,
                        restUiState = restUiState
                    )
                    BreakPauseButtons(
                        viewModel = viewModel,
                        focusUiState = focusUiState
                    )
                }


            is PomodoroState.Resting -> {
                Box(

                ) {
                    CustomCircularProgressIndicator(
                        progress = 1f - (restUiState.restDuration.toFloat() / restUiState.initialRestDuration.toFloat()),
                        modifier = Modifier.size(250.dp),
                        progressColor = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = viewModel.formatter(restUiState.restDuration),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    BreakPauseButtons(
                        viewModel = viewModel,
                        focusUiState = focusUiState
                    )
                }
            }

            is PomodoroState.Paused -> {
                Text("Paused", style = MaterialTheme.typography.headlineSmall)
                BreakPauseButtons(
                    viewModel = viewModel,
                    focusUiState = focusUiState
                )
            }

            is PomodoroState.Finished -> {
                Text("All sessions completed!", style = MaterialTheme.typography.headlineMedium)
                Button(onClick = { viewModel.reset() }) {
                    Text("Restart")
                }
            }

            is PomodoroState.Error -> {
                val error = pomodoroState as PomodoroState.Error
                Text("Error: ${error.message}", color = Color.Red)
                Button(onClick = { viewModel.reset() }) {
                    Text("Reset")
                }
            }
        }
    }
}







@Preview (showBackground = true)
@Composable
fun CountDownTimerPreview () {
    CountDownTimer()
}