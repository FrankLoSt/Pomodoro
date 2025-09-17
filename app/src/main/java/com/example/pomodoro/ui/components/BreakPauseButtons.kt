package com.example.pomodoro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.R
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.ui.theme.PomodoroTheme

@Composable
fun BreakPauseButtons (
    viewModel: ViewModelCountDown,
    focusUiState: FocusUiState,

) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { viewModel.breakFun() },
            modifier = Modifier
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.supperbing),
                    contentDescription = null,
                    )
                Text(
                    text = stringResource(R.string.break_fun),
                    style = MaterialTheme.typography.titleLarge,
                    )
            }
        }

    Button (
            onClick = { viewModel.togglePauseResume() },

            modifier = Modifier.width(200.dp),
             colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
        Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
            Image(
                painter = painterResource(R.drawable.supperbing),
                contentDescription = null,
                )
            Text(
                        text = if (focusUiState.isPause) stringResource(R.string.resume) else stringResource(
                            R.string.pause
                        ),
                        style = MaterialTheme.typography.titleLarge,
                    )
        }
    }
}
}


@Preview
@Composable
fun BreakPauseButtonsPreview () {
    PomodoroTheme {
        BreakPauseButtons(
            viewModel = viewModel(),
            focusUiState = FocusUiState(),
        )
    }
}