package com.example.pomodoro.ui.Screen1

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.R
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.TimerStatus
import com.example.pomodoro.ui.theme.PomodoroTheme

@Composable
fun BreakButton (
    breakFun: () -> Unit = {},
    breakFunDialog: () -> Unit = {},
) {
    var isSure: Boolean? by rememberSaveable { mutableStateOf(null) }
    Button(
        onClick = {
            isSure = true
        },
        //modifier = Modifier.width(150.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.breakbut2),
                contentDescription = null,
                modifier = Modifier.size(width=100.dp, height=50.dp)
            )
        }
    }
    if(isSure == true) {
        BreakAlertDialog(
            onDismissRequest = {
                isSure = false },
            confirmButton = breakFun,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakAlertDialog (
    onDismissRequest: () -> Unit = {},
    confirmButton: () -> Unit = {},
    @StringRes title: Int = R.string.break_title,
    @StringRes text: Int = R.string.break_text
) {
    AlertDialog(
        title = {Text(text = stringResource(title))},
        text = {Text(text = stringResource(text))},
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = { confirmButton()}) {Text("Yes")}
        } ,
        dismissButton = {
            TextButton(onClick = { onDismissRequest()}) {Text("No")}
        }
    )
}

@Composable
fun PauseButton (
    togglePauseResume: () -> Unit = {},
    focusUiState: FocusUiState,
) {
    Button (
        onClick = { togglePauseResume() },
        //modifier = Modifier.width(150.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (focusUiState.focusTimerStatus == TimerStatus.PAUSED) painterResource(R.drawable.resumebut) else painterResource(R.drawable.pausebutton2),
                contentDescription = null,
                modifier = Modifier.size(width = 100.dp, height = 50.dp)
            )
        }
    }
}


@Preview
@Composable
fun BreakPauseButtonsPreview () {
    PomodoroTheme {
        BreakButton()
    }
}

@Preview
@Composable
fun BreakPauseButtonsPreview2 () {
    PomodoroTheme {
        PauseButton(
            focusUiState = FocusUiState()
        )
    }
}
@Preview
@Composable
fun BreakAlertDialogPreview () {
    PomodoroTheme {
        BreakAlertDialog()
    }
}