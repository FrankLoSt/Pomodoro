package com.example.pomodoro.ui.Screen1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.R
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.theme.PomodoroTheme

@Composable
fun CircularProgressBar (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    formatter: (Int) ->  String = { minutes -> "$minutes min"}
) {
    Text(
        text = if ( restUiState.isStudying && focusUiState.isRunning ) stringResource(R.string.Studying) else stringResource(R.string.Taking_a_break),
        style = MaterialTheme.typography.titleLarge
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(300.dp),
    ) {
        //rest countdown Screen
        if (restUiState.isStudying  && focusUiState.isRunning  ) { //initial stage: isStudying = false, restDuration > 0 => automatically display the rest countdown screen
            val progress = focusUiState.studyProgress()
            CustomCircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(300.dp),
            )
        }
        //progress focus time
        else {
            val progress = restUiState.restProgress()
            CustomCircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(300.dp),
            )
        }


        Text(
            text = if(focusUiState.isRunning && restUiState.isStudying )  formatter(focusUiState.duration) else  formatter(restUiState.restDuration),
            style = MaterialTheme.typography.displayLarge
        )
    } //box for progress bar and text
}

@Preview
@Composable
fun CircularProgressBarPreview () {
    PomodoroTheme {
        CircularProgressBar(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
        )
    }
}