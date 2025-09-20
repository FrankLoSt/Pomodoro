package com.example.pomodoro.ui.Screen1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState

@Preview(
    name = "Expanded Landscape",
    widthDp = 800,
    heightDp = 400,
    showBackground = true
)
@Composable
fun PreviewExpanded () {
    ExpandedScreen(
        focusUiState = FocusUiState(),
        restUiState = RestUiState(),
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
        startCountDown = {},
        breakFun = {},
        togglePauseResume = {},
        toggleisFinished = {},
    )
}




@Composable
fun ExpandedScreen (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    setDurationMinutes : (Int) -> Unit = {},
    setRestDurationMinutes : (Int) -> Unit = {},
    setSessions : (Int) -> Unit = {},
    startCountDown: () -> Unit ,
    breakFun: () -> Unit ,
    togglePauseResume: () -> Unit,
    toggleisFinished: () -> Unit,
    formatter: (Int) ->  String = { minutes -> "$minutes min"}
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = scrollState
            )
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if(restUiState.isShowingMenu) {
            Row(
                modifier  = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropDown(
                    listSessions = focusUiState.listSessions,
                    listFocusDuration = focusUiState.listFocusDuration,
                    listRestDuration = restUiState.listRestDuration,
                    setDurationMinutes = setDurationMinutes,
                    setRestDurationMinutes = setRestDurationMinutes,
                    setSessions = setSessions,
                )
                CountDownButton(
                    startCountDown = startCountDown,
                )
            }
        }
        if(focusUiState.isFinished) {
            AlertDialog1(
                onDismiss = toggleisFinished,
                duration = focusUiState.duration
            )
        }
        if(!restUiState.isShowingMenu) {
            Row(
                modifier  = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressBar(
                    focusUiState = focusUiState,
                    restUiState = restUiState,
                    formatter = formatter
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    BreakButton(
                        breakFun = breakFun,
                        breakFunDialog = togglePauseResume
                    )
                    PauseButton(
                        togglePauseResume = togglePauseResume,
                        focusUiState = focusUiState,
                    )
                }
            }
        }
    }
}