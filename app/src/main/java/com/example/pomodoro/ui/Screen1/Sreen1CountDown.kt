package com.example.pomodoro.ui.Screen1


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass // ✅
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pomodoro.R
import com.example.pomodoro.data.AppPhase
import com.example.pomodoro.data.TimerState



@Composable
fun Screen1Portrait (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    toggleisFinished: () -> Unit,
    breakFun: () -> Unit,
    togglePauseResume: () -> Unit,
    breakFunDialog: () -> Unit,
) {
    PhonePortraitCircularProgressBar(
        focusUiState = focusUiState,
        restUiState = restUiState,
        togglePauseResume = togglePauseResume,
        breakFun = breakFun,
        breakFunDialog = breakFunDialog
    )

    if (focusUiState.appPhrase == AppPhase.FINISHED) {
        AlertDialog1(
            onDismiss = toggleisFinished,
            duration = focusUiState.duration
        )
    }
}



@Preview(
    name = "Compact Portrait",
    showBackground = true,
    widthDp =  412,
    heightDp =  915
)
@Composable
fun Screen1Preview () {
    PomodoroTheme {
        Screen1Portrait(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            toggleisFinished = {},
            breakFun = {},
            togglePauseResume = {},
            breakFunDialog = {},
        )
    }
}




@Preview(
    name = "Compact Portrait",
    showBackground = true,
    widthDp = 915,
    heightDp = 412
)
@Composable
fun Screen1PreviewLandscape () {
    PomodoroTheme {
        Screen1Portrait(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            toggleisFinished = {},
            breakFun = {},
            togglePauseResume = {},
            breakFunDialog = {},
        )
    }
}

