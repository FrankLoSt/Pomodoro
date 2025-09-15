package com.example.pomodoro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
        Button (
            onClick = { },
            modifier = Modifier.width(130.dp)
        ) {
            Text(
                text = stringResource(R.string.break_fun),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Button (
            onClick = {  },
            modifier = Modifier.width(130.dp),
             colors = ButtonDefaults.buttonColors(if(focusUiState.isPause) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if(focusUiState.isPause) stringResource(R.string.resume) else stringResource(R.string.pause),
                style = MaterialTheme.typography.labelLarge,
            )
        }

    }
}

@Preview
@Composable
fun BreakPauseButtonsPreview () {
    BreakPauseButtons(
        viewModel = viewModel(),
        focusUiState = FocusUiState(),
    )
}