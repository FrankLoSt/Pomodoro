package com.example.pomodoro.ui.countdown

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.pomodoro.R
import com.example.pomodoro.ui.detectScreenShape
import com.example.pomodoro.ui.pickmonster.FontSize
import com.example.pomodoro.ui.pickmonster.LocalFontSize
import com.example.pomodoro.ui.pickmonster.LocalSpacing
import com.example.pomodoro.ui.pickmonster.MonsterInfo
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.Spacing




@Composable
fun PhonePortraitCircularProgressBar (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    monsterIndex: Int = 0,
    togglePauseResume: () -> Unit = {},
    breakFun: () -> Unit = {},
    breakFunDialog: () -> Unit = {},
    countDownText: String = "25:00",
    monsterList: List<MonsterInfo> = listOf(MonsterInfo(imageId = R.drawable.treemonster, "Bet Rot", "Ruin your future and mental health")),
    onNavigate: () -> Unit,
    windowSizeClass: WindowSizeClass? = null,
) {



    val windowSizeClass = windowSizeClass


    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {

        val maxWidth: Dp = this.maxWidth
        val maxHeight: Dp = this.maxHeight

        when (focusUiState.appPhrase) {

            AppPhase.FOCUSING -> ProgressViewPortraitMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,
                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )

            AppPhase.RESTING -> ProgressViewPortraitMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,

                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )

            AppPhase.FINISHED -> ProgressViewPortraitMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,

                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,

                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )
            else -> {}
        }
    } //box for progress bar and text
}


@Composable
fun ProgressViewPortraitMode (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    windowSizeClass: WindowSizeClass?,
    maxWidth: Dp,
    maxHeight: Dp,
    monsterIndex: Int,
    countDownText: String,
    onNavigate: () -> Unit,
    togglePauseResume: () -> Unit,
    breakFun: () -> Unit,
    breakFunDialog: () -> Unit,
    monsterList: List<MonsterInfo>
){
    val baseSize = minOf(maxWidth, maxHeight) // 👈 Use the smaller dimension
    val spacing = baseSize * 0.05f
    val fontSizeSmall = (baseSize.value * 0.04f).sp
    val fontSizeLarge = (baseSize.value * 0.2f).sp
    val buttonSize = baseSize * 0.2f
    val progressSize = baseSize * 0.8f


    val monster = monsterList[monsterIndex].imageId

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text =
                when (focusUiState.appPhrase) {
                    AppPhase.FOCUSING -> stringResource(R.string.batling) + " " + monsterList[monsterIndex].name
                    AppPhase.RESTING -> stringResource(R.string.Taking_a_break)
                    else -> stringResource(R.string.finished)
                },
            fontSize = (maxWidth.value * 0.06f).toInt().sp,
            modifier = Modifier.padding(bottom = spacing),
            fontFamily = FontFamily(Font(R.font.jersey))
        )
        Text(
            text = "Session: ${focusUiState.sessions} /${focusUiState.totalSessions}",
            fontSize = (maxWidth.value * 0.05f).toInt().sp,
            fontFamily = FontFamily(Font(R.font.jersey))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight * 0.4f),
            contentAlignment = Alignment.Center
        ) {
            CustomCircularProgressIndicator(
                progress =
                    when (focusUiState.appPhrase) {
                        AppPhase.FOCUSING -> focusUiState.studyProgress()
                        AppPhase.RESTING -> restUiState.restProgress()
                        else -> (1f)
                    },
                modifier = Modifier.size(progressSize),
                blockSize = if((windowSizeClass?.widthSizeClass) == WindowWidthSizeClass.Compact) baseSize.value * 0.08f else baseSize.value * 0.05f
            )
            Image(
                painter = painterResource(monster),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        if ((windowSizeClass?.widthSizeClass) == WindowWidthSizeClass.Compact) baseSize * 0.45f else baseSize * 0.3f
                    )
                    .align(Alignment.Center)
            )
            Log.e("ProgressViewModes", "ProgressViewModes: ${windowSizeClass?.widthSizeClass}")

        }
        Text("Tag: Working", fontSize = fontSizeSmall, fontFamily = FontFamily(Font(R.font.jersey)))

        Text(countDownText, fontSize = fontSizeLarge, fontFamily = FontFamily(Font(R.font.jersey)))

        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            BreakButton(
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                modifier = Modifier.size(buttonSize),
                onNavigate = onNavigate
            )
            PauseButton(
                togglePauseResume = togglePauseResume,
                focusUiState = focusUiState,
                modifier = Modifier.size(buttonSize)
            )
        }
    }
}




@Composable
fun PhoneLandscapeCircularProgressBar (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    monsterIndex: Int = 0,
    monsterList: List<MonsterInfo>,
    togglePauseResume: () -> Unit = {},
    breakFun: () -> Unit = {},
    breakFunDialog: () -> Unit = {},
    countDownText: String = "25:00",
    windowSizeClass: WindowSizeClass?,
    onNavigate: () -> Unit = {}
) {


    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        val maxWidth: Dp = this.maxWidth
        val maxHeight: Dp = this.maxHeight




        when (focusUiState.appPhrase) {

            AppPhase.FOCUSING -> LandscapeProgressBarViewMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,
                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )

            AppPhase.RESTING ->LandscapeProgressBarViewMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,
                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )

            AppPhase.FINISHED -> LandscapeProgressBarViewMode(
                focusUiState = focusUiState,
                restUiState = restUiState,
                windowSizeClass = windowSizeClass,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                monsterIndex = monsterIndex,
                countDownText = countDownText,
                onNavigate = onNavigate,
                togglePauseResume = togglePauseResume,
                breakFun = breakFun,
                breakFunDialog = breakFunDialog,
                monsterList = monsterList
            )

            else -> {}
        }
    } //box for progress bar and text
}

@Composable
fun LandscapeProgressBarViewMode (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    windowSizeClass: WindowSizeClass?,
    maxWidth: Dp,
    maxHeight: Dp,
    monsterIndex: Int,
    countDownText: String,
    onNavigate: () -> Unit,
    togglePauseResume: () -> Unit,
    breakFun: () -> Unit,
    breakFunDialog: () -> Unit,
    monsterList: List<MonsterInfo>
){
    val maxWidth: Dp = maxWidth
    val maxHeight: Dp = maxHeight


    val baseSize = minOf(maxWidth, maxHeight) // 👈 Use the smaller dimension

    val spacing = baseSize * 0.05f
    val fontSizeSmall = (baseSize.value * 0.04f).sp
    val fontSizeLarge = (baseSize.value * 0.2f).sp
    val buttonSize = baseSize * 0.2f

    val imageSize = baseSize * 0.5f

    val monster = monsterList[monsterIndex].imageId

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.8f),
            contentAlignment = Alignment.Center
        ) {
            CustomCircularProgressIndicator(
                progress =
                    when (focusUiState.appPhrase) {
                        AppPhase.FOCUSING -> focusUiState.studyProgress()
                        AppPhase.RESTING -> restUiState.restProgress()
                        else -> (1f)
                    },
                modifier = Modifier.size(baseSize),
                blockSize =
                    if((windowSizeClass?.widthSizeClass) == WindowWidthSizeClass.Compact)
                    baseSize.value * 0.08f
                    else baseSize.value * 0.05f
            )
            Image(
                painter = painterResource(monster),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text =
                    when (focusUiState.appPhrase) {
                        AppPhase.FOCUSING -> stringResource(R.string.batling) + " " + monsterList[monsterIndex].name
                        AppPhase.RESTING -> stringResource(R.string.Taking_a_break)
                        else -> stringResource(R.string.finished)
                    },
                fontSize = (baseSize.value * 0.05f).toInt().sp,
                modifier = Modifier.padding(bottom = spacing),
                fontFamily = FontFamily(Font(R.font.jersey))
            )
            Text(
                text = "Session: ${focusUiState.sessions} /${focusUiState.totalSessions}",
                fontSize = (baseSize.value * 0.05f).toInt().sp,
                fontFamily = FontFamily(Font(R.font.jersey))
            )
            Text(
                "Tag: Working",
                fontSize = fontSizeSmall,
                fontFamily = FontFamily(Font(R.font.jersey))
            )
            Text(
                countDownText,
                fontSize = fontSizeLarge,
                fontFamily = FontFamily(Font(R.font.jersey))
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreakButton(
                    breakFun = breakFun,
                    breakFunDialog = breakFunDialog,
                    modifier = Modifier.size(buttonSize),
                    onNavigate = onNavigate
                )
                PauseButton(
                    togglePauseResume = togglePauseResume,
                    focusUiState = focusUiState,
                    modifier = Modifier.size(buttonSize)
                )
            }
        }
    }
}





@Preview(
    name = "Compact Portrait",
)
@Composable
fun PhonePortraitCircularProgressBarP () {
    MyAppTheme {
        PhonePortraitCircularProgressBar(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            onNavigate = {},
        )
    }
}

@Preview(
    name = "Phone landscape",
    showBackground = true,
    widthDp = 915,
    heightDp = 400
)
@Composable
fun PhoneLandScapeCircularProgressBarP () {
    MyAppTheme {
        PhoneLandscapeCircularProgressBar(
            focusUiState = FocusUiState(),
            restUiState = RestUiState(),
            onNavigate = {},
            windowSizeClass = null,
            monsterList = emptyList()
        )
    }
}









@Composable
fun TabletPortraitCircularProgressBar (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    @DrawableRes monster: Int = R.drawable._07_1,
    togglePauseResume: () -> Unit = {},
    breakFun: () -> Unit = {},
    breakFunDialog: () -> Unit = {},
    countDownText: String = "25:00",
    onNavigate: () -> Unit = {}
) {
    val localFontSize: FontSize = LocalFontSize.current
    val localSpacing: Spacing = LocalSpacing.current

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        val maxWidth: Dp = this.maxWidth
        val maxHeight: Dp = this.maxHeight


        when (focusUiState.appPhrase) {
            AppPhase.FOCUSING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.batling),
                        fontSize = (maxWidth.value * 0.05f).toInt().sp,
                        modifier = Modifier.padding(bottom = localSpacing.medium),
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxHeight * 0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        val progress = focusUiState.studyProgress()
                        CustomCircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.size(maxWidth * 0.9f),
                            blockSize = maxWidth.value * 0.1f,
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(maxWidth * 0.5f)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = "Tag: Working",
                        fontSize = (maxWidth.value * 0.04f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )
                    Text(
                        text  = countDownText,
                        fontSize = (maxWidth.value * 0.2f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BreakButton(
                            breakFun = breakFun,
                            breakFunDialog = breakFunDialog,
                            modifier = Modifier.size(maxWidth * 0.2f),
                            onNavigate = onNavigate
                        )
                        PauseButton(
                            togglePauseResume = togglePauseResume,
                            focusUiState = focusUiState,
                            modifier = Modifier.size(maxWidth * 0.2f)
                        )
                    }
                }
            }

            AppPhase.RESTING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val progress = restUiState.restProgress()
                    Text(
                        text = stringResource(R.string.Taking_a_break),
                        fontSize = localFontSize.large,
                        modifier = Modifier.padding(bottom = localSpacing.medium)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxHeight * 0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomCircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.size(maxWidth * 0.8f),
                            blockSize = maxWidth.value * 0.1f,
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(maxWidth * 0.5f)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = "Tag: Working",
                        fontSize = (maxWidth.value * 0.04f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )
                    Text(
                        text  = countDownText,
                        fontSize = (maxWidth.value * 0.2f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BreakButton(
                            breakFun = breakFun,
                            breakFunDialog = breakFunDialog,
                            modifier = Modifier.size(maxWidth * 0.2f)
                        )
                        PauseButton(
                            togglePauseResume = togglePauseResume,
                            focusUiState = focusUiState,
                            modifier = Modifier.size(maxWidth * 0.2f)
                        )
                    }
                }
            }

            AppPhase.FINISHED -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxHeight * 0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomCircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.size(maxWidth * 0.8f),
                            blockSize = maxWidth.value * 0.1f,
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(maxWidth * 0.5f)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = "Tag: Working",
                        fontSize = (maxWidth.value * 0.04f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )
                    Text(
                        text = countDownText,
                        fontSize = (maxWidth.value * 0.2f).toInt().sp,
                        fontFamily = FontFamily(Font(R.font.jersey))
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BreakButton(
                            breakFun = breakFun,
                            breakFunDialog = breakFunDialog,
                            modifier = Modifier.size(maxWidth * 0.2f)
                        )
                        PauseButton(
                            togglePauseResume = togglePauseResume,
                            focusUiState = focusUiState,
                            modifier = Modifier.size(maxWidth * 0.2f)
                        )
                    }
                }
            }
            else -> {}
        }
    } //box for progress bar and text
}

@Composable
fun TabletLandscapeCircularProgressBar (
    focusUiState: FocusUiState,
    restUiState: RestUiState,
    monster: Int = R.drawable._07_1,
    togglePauseResume: () -> Unit = {},
    breakFun: () -> Unit = {},
    breakFunDialog: () -> Unit = {},
    countDownText: String = "25:00"
) {
    val localFontSize: FontSize = LocalFontSize.current
    val localSpacing: Spacing = LocalSpacing.current

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        val maxWidth: Dp = this.maxWidth
        val maxHeight: Dp = this.maxHeight


        val baseSize = minOf(maxWidth, maxHeight) // 👈 Use the smaller dimension

        val spacing = baseSize * 0.05f
        val fontSizeSmall = (baseSize.value * 0.04f).sp
        val fontSizeLarge = (baseSize.value * 0.2f).sp
        val buttonSize = baseSize * 0.2f

        val imageSize = baseSize * 0.5f

        when (focusUiState.appPhrase) {

            AppPhase.FOCUSING -> {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomCircularProgressIndicator(
                            progress = focusUiState.studyProgress(),
                            modifier = Modifier.size(baseSize),
                            blockSize = baseSize.value * 0.1f
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize)
                                .align(Alignment.Center)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.batling),
                            fontSize = (baseSize.value * 0.05f).toInt().sp,
                            modifier = Modifier.padding(bottom = spacing),
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            "Tag: Working",
                            fontSize = fontSizeSmall,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            countDownText,
                            fontSize = fontSizeLarge,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BreakButton(
                                breakFun = breakFun,
                                breakFunDialog = breakFunDialog,
                                modifier = Modifier.size(buttonSize)
                            )
                            PauseButton(
                                togglePauseResume = togglePauseResume,
                                focusUiState = focusUiState,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
            }

            AppPhase.RESTING -> {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomCircularProgressIndicator(
                            progress = focusUiState.studyProgress(),
                            modifier = Modifier.size(baseSize),
                            blockSize = baseSize.value * 0.1f
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize)
                                .align(Alignment.Center)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.batling),
                            fontSize = (baseSize.value * 0.05f).toInt().sp,
                            modifier = Modifier.padding(bottom = spacing),
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            "Tag: Working",
                            fontSize = fontSizeSmall,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            countDownText,
                            fontSize = fontSizeLarge,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BreakButton(
                                breakFun = breakFun,
                                breakFunDialog = breakFunDialog,
                                modifier = Modifier.size(buttonSize)
                            )
                            PauseButton(
                                togglePauseResume = togglePauseResume,
                                focusUiState = focusUiState,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
            }

            AppPhase.FINISHED -> {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomCircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.size(baseSize),
                            blockSize = baseSize.value * 0.1f
                        )
                        Image(
                            painter = painterResource(monster),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize)
                                .align(Alignment.Center)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.batling),
                            fontSize = (baseSize.value * 0.05f).toInt().sp,
                            modifier = Modifier.padding(bottom = spacing),
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            "Tag: Working",
                            fontSize = fontSizeSmall,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Text(
                            countDownText,
                            fontSize = fontSizeLarge,
                            fontFamily = FontFamily(Font(R.font.jersey))
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BreakButton(
                                breakFun = breakFun,
                                breakFunDialog = breakFunDialog,
                                modifier = Modifier.size(buttonSize)
                            )
                            PauseButton(
                                togglePauseResume = togglePauseResume,
                                focusUiState = focusUiState,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    } //box for progress bar and text
}


@Preview(
    name = "Expanded Landscape",
    widthDp = 1120,
    heightDp = 1600,
    showBackground = true
)
@Composable
fun CircularProgressBarPortraitTabletPreview () {
    MyAppTheme {

    }
}

@Preview(
    name = "Expanded Landscape",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun CircularProgressBarTabletLandscapePreview () {
    MyAppTheme {

    }
}