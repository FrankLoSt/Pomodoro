package com.example.pomodoro.ui.Screen1

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.R
import com.example.pomodoro.data.FocusUiState
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
        var isBreak by rememberSaveable { mutableStateOf(false) }
        Button(
            onClick = {
                isBreak = true
                viewModel.breakFun()
                      },
            modifier = Modifier
                .width(150.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if ( isBreak) painterResource(R.drawable.breakbutton) else painterResource (R.drawable.breakbut2),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }

    Button (
            onClick = { viewModel.togglePauseResume() },
            modifier = Modifier.width(150.dp),
             colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (focusUiState.isPause) painterResource(R.drawable.resumebut) else painterResource(R.drawable.pausebutton2),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
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