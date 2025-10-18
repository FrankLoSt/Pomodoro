package com.example.pomodoro.ui.countdown


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodoro.ui.theme.PomodoroTheme

import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.pomodoro.MainActivity
import com.example.pomodoro.R
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
    windowSizeClass: WindowSizeClass,
    monsterId: Int = 0,
    monsterList: List<MonsterInfo> = emptyList(),
    onNavigate: () -> Unit = {},
    countDownText: String
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
            monsterList = monsterList,
            onNavigate = onNavigate,
            windowSizeClass = windowSizeClass,
            countDownText = countDownText
            )

        is ScreenShape.PhoneLandscape -> PhoneLandscapeCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog,
            windowSizeClass =  windowSizeClass,
            monsterIndex = monsterId,
            monsterList = monsterList,
            onNavigate = onNavigate,
            countDownText = countDownText
        )

        is ScreenShape.TabletPortrait -> PhonePortraitCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog,
            monsterIndex = monsterId,
            monsterList = monsterList,
            onNavigate = onNavigate,
            windowSizeClass = windowSizeClass,
            countDownText = countDownText
        )

        is ScreenShape.TabletLandscape -> PhoneLandscapeCircularProgressBar(
            focusUiState = focusUiState,
            restUiState = restUiState,
            togglePauseResume = togglePauseResume,
            breakFun = breakFun,
            breakFunDialog = breakFunDialog,
            windowSizeClass = windowSizeClass,
            monsterIndex = monsterId,
            monsterList = monsterList,
            onNavigate = onNavigate,
            countDownText = countDownText
        )
    }

    if (focusUiState.appPhrase == AppPhase.FINISHED) {
        AlertDialog1(
            onDismiss = onDismiss,
            duration = focusUiState.duration
        )
    }
}




@Composable
fun AlertDialog1 (
    onDismiss: () -> Unit,
    @StringRes text1: Int = R.string.congrat_mess,
    @StringRes text2: Int = R.string.you_ve_focused_for,
    @StringRes text3: Int = R.string.you_ve_earned_an_armor,
    @DrawableRes image: Int = R.drawable.amor,
    duration: Int = 1
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( //text1
                    text = stringResource(text1),
                    style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(//text2
                    text = stringResource(text2) + " $duration minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(//text3
                    text = stringResource(text3),
                )
                Image(//image
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Button(onClick = onDismiss) {
                    Text("Okay")
                }
            }
        }
    }
}





