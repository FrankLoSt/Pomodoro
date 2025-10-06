package com.example.pomodoro.ui.Screen1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.R
import com.example.pomodoro.data.AppPhase
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.google.apps.card.v1.Columns
import com.google.apps.card.v1.Image

@Composable
fun CircularProgressBar (
    modifier: Modifier = Modifier,
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    formatter: (Int) ->  String = { minutes -> "$minutes min"}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (focusUiState.appPhrase == AppPhase.FOCUSING)
                stringResource(R.string.Studying)
            else if (focusUiState.appPhrase == AppPhase.RESTING)
                stringResource(R.string.Taking_a_break)
            else "",
            style = MaterialTheme.typography.titleLarge
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.wrapContentSize(),
        ) {
            //rest countdown Screen
            if (focusUiState.appPhrase == AppPhase.FOCUSING) { //initial stage: isStudying = false, restDuration > 0 => automatically display the rest countdown screen
                val progress = focusUiState.studyProgress()
                CustomCircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(300.dp),
                )
            }
            //progress focus time
            else if(focusUiState.appPhrase == AppPhase.RESTING){
                val progress = restUiState.restProgress()
                CustomCircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(300.dp),
                )
            } else if (focusUiState.appPhrase == AppPhase.FINISHED) {
                CustomCircularProgressIndicator(
                    progress = 0f,
                    modifier = Modifier.size(300.dp),
                )
            }
            Image(
                painter = painterResource(R.drawable._07_1),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
        } //box for progress bar and text
    }
}

@Preview
@Composable
fun CircularProgressBarPreview2 () {
    PomodoroTheme {
        CircularProgressBar(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
        )
    }
}

@Preview(
    name = "Expanded Landscape",
    widthDp = 800,
    heightDp = 1280,
    showBackground = true
)
@Composable
fun CircularProgressBarPreview () {
    PomodoroTheme {
        CircularProgressBar(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
        )
    }
}