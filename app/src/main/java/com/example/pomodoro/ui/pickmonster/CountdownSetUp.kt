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
fun MyBottomSheetScreen() {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val maxWidth: Int = LocalWindowInfo.current.containerSize.width
    val maxHeight: Int = LocalWindowInfo.current.containerSize.height

    val fontSize: FontSize = LocalFontSize.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                scope.launch { sheetState.show() }
                Log.d("DEBUG", "MyBottomSheetScreen: Clicked")
            }
        ) {
            Text("Open Bottom Sheet")
        }
        if (true) {
            ModalBottomSheet(
                onDismissRequest = { /* handle dismiss */ },
                sheetState = sheetState,
                modifier = Modifier.fillMaxSize(),
            ) {
                DropDownPortrait(
                    setDurationMinutes = {},
                    setRestDurationMinutes = {},
                    setSessions = {},
                    windowSizeClass = null
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun PreviewBottomSheet () {
    MyAppTheme {
        MyBottomSheetScreen()
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
        MyBottomSheetScreen()
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