package com.example.pomodoro.ui.pickmonster

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pomodoro.R
import com.example.pomodoro.ui.countdown.DropDownPortrait
import kotlinx.coroutines.launch










@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetScreen(
    fightToggleDialog: () -> Unit  = {},
    confirmBut: () ->Unit = {},
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
    windowSizeClass: WindowSizeClass?
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = { /* handle dismiss */ },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize(),
    ) {
        DropDownPortrait(
            setDurationMinutes = setDurationMinutes,
            setRestDurationMinutes = setRestDurationMinutes,
            setSessions = setSessions,
            listFocusDuration = listFocusDuration,
            listRestDuration = listRestDuration,
            listSessions = listSessions,
            windowSizeClass = windowSizeClass,
            fightToggleDialog = fightToggleDialog,
            confirmBut = confirmBut
        )
    }
}


@Preview(
    showBackground = true,
)
@Composable
fun PreviewBottomSheet () {
    MyAppTheme {

    }
}

@Preview(
    showBackground = true,
    widthDp = 900,
    heightDp = 400
)
@Composable
fun LandScapePreviewBottomSheet () {
    MyAppTheme {

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersistentBottomSheet() {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                scope.launch { sheetState.bottomSheetState.expand() }
            }
        ) {
            Text("Open Bottom Sheet")
        }
        Button(
            onClick = {
                scope.launch { sheetState.bottomSheetState.hide() }
            }
        ){
            Text("Close Bottom Sheet")
        }
        if (sheetState.bottomSheetState.isVisible) {
            BottomSheetScaffold(
                scaffoldState = sheetState,
                sheetContent = {
                    Text("This is the sheet content", Modifier.padding(16.dp))
                },
                sheetPeekHeight = 300.dp,


            ) {
                Button(onClick = { scope.launch { sheetState.bottomSheetState.expand() } }) {
                    Text("Show Sheet")
                }
            }
        }

    }
}