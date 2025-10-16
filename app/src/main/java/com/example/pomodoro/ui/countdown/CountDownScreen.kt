package com.example.pomodoro.ui.countdown


import androidx.annotation.DrawableRes
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodoro.ui.theme.PomodoroTheme

import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.navigation.NavHostController
import com.example.pomodoro.MainActivity
import com.example.pomodoro.ui.ScreenShape
import com.example.pomodoro.ui.detectScreenShape
import com.example.pomodoro.ui.pickmonster.MonsterInfo


@Composable
fun CountDownScreen(
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    onDismiss: () -> Unit,
    breakFun: () -> Unit,
    togglePauseResume: () -> Unit,
    breakFunDialog: () -> Unit,
    navHostController: NavHostController? = null,
    windowSizeClass: WindowSizeClass,
    monsterId: Int = 0,
    monsterList: List<MonsterInfo> = emptyList()

) {
    val windowSizeCheck = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val screenWidth = with(density) { windowSizeCheck.width.toDp() }.value.toInt()
    val screenHeight = with(density) { windowSizeCheck.height.toDp() }.value.toInt()

    val windowSizeClass = windowSizeClass
    val screenShape = detectScreenShape(
        windowSizeClass.widthSizeClass ,
        windowSizeClass.heightSizeClass,
        screenWidth,
        screenHeight
    )

    when (screenShape) {
        is ScreenShape.PhonePortrait -> PhonePortraitCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog,
            monsterIndex = monsterId,
            monsterList = monsterList
            )
        is ScreenShape.PhoneLandscape -> PhoneLandscapeCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog
        )
        is ScreenShape.TabletPortrait -> TabletPortraitCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog
        )
        is ScreenShape.TabletLandscape -> TabletLandscapeCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog
        )

    }

    if (focusUiState.appPhrase == AppPhase.FINISHED) {
        AlertDialog1(
            onDismiss = onDismiss,
            duration = focusUiState.duration
        )
    }
}



@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(
    name = "Compact Portrait",
    showBackground = true,
    widthDp =  412,
    heightDp =  915
)
@Composable
fun Screen1Preview () {
    PomodoroTheme {
        val activity: MainActivity = MainActivity()
        CountDownScreen(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            onDismiss = {},
            breakFun = {},
            togglePauseResume = {},
            breakFunDialog = {},
            windowSizeClass = calculateWindowSizeClass( activity )
        )
    }
}




@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(
    name = "Compact Portrait",
    showBackground = true,
    widthDp = 915,
    heightDp = 412
)
@Composable
fun Screen1PreviewLandscape () {
    PomodoroTheme {
        val activity: MainActivity = MainActivity()
        CountDownScreen(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            onDismiss = {},
            breakFun = {},
            togglePauseResume = {},
            breakFunDialog = {},
            windowSizeClass = calculateWindowSizeClass( activity )
        )
    }
}

