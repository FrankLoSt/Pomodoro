package com.example.pomodoro.ui.Screen1

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.pomodoro.R
import com.example.pomodoro.data.InitSetUpState

data class Spacing(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)

data class FontSize(
    val small: TextUnit,
    val medium: TextUnit,
    val large: TextUnit,
    val extraLarge: TextUnit
)


val LocalSpacing: ProvidableCompositionLocal<Spacing> = compositionLocalOf { Spacing(8.dp, 16.dp, 24.dp, 32.dp) }
val LocalFontSize: ProvidableCompositionLocal<FontSize> = compositionLocalOf { FontSize(12.sp, 14.sp, 18.sp, 24.sp) }// default fallback }

@Composable
fun MyAppTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    content: @Composable () -> Unit
) {
    val windowSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current

    val screenWidth = with(density) { windowSize.width.toDp()}.value.toInt()
    val screenHeight = with(density) { windowSize.height.toDp()}.value.toInt()

    Log.d("MyAppTheme", "screenWidth: $screenWidth, screenHeight: $screenHeight")


    // Adaptive spacing
    val spacing: Spacing = when {
        screenWidth < 600 || screenHeight < 600 -> Spacing(8.dp, 16.dp, 24.dp, 32.dp)
        screenWidth < 900 || screenHeight < 900 -> Spacing(12.dp, 20.dp, 28.dp, 36.dp)
        else -> Spacing(16.dp, 24.dp, 32.dp, 48.dp)
    }

    // Adaptive font size
    val fontSize: FontSize = when {
        screenWidth < 500 && screenHeight in 400 .. 1000 -> FontSize(12.sp, 14.sp, 18.sp, 24.sp)
        screenWidth in 400 .. 1000 && screenHeight < 500 -> FontSize(12.sp, 14.sp, 18.sp, 24.sp)
        screenWidth <1000 && screenHeight in 1000..1300 -> FontSize(14.sp, 16.sp, 20.sp, 28.sp)
        screenWidth in 1000..1300 && screenHeight < 1000 -> FontSize(14.sp, 16.sp, 20.sp, 28.sp)

        else -> FontSize(12.sp, 14.sp, 18.sp, 24.sp)                // small phones
    }


    CompositionLocalProvider(
        LocalSpacing provides spacing,
        LocalFontSize provides fontSize
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes
        ) {
            content()
        }
    }
}



@Composable
fun DashBoard (
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    toggleSetUpPopup: () -> Unit,
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
    confirmBut: () -> Unit = {},
    initSetUpState: InitSetUpState
) {
    val windowSizeCheck = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current

    val screenWidth = with(density) { windowSizeCheck.width.toDp()}.value.toInt()
    val screenHeight = with(density) { windowSizeCheck.height.toDp()}.value.toInt()

    Log.d("MyAppTheme", "screenWidth: $screenWidth, screenHeight: $screenHeight")

    if (windowSize == WindowWidthSizeClass.Compact && screenHeight in 800 .. 1500 ) {
        DashBoardPhonePortrait(
            modifier,
            toggleSetUpPopup = toggleSetUpPopup,
            setDurationMinutes = setDurationMinutes,
            setRestDurationMinutes = setRestDurationMinutes,
            setSessions = setSessions,
            listFocusDuration = listFocusDuration,
            listRestDuration = listRestDuration,
            listSessions = listSessions,
            confirmBut = confirmBut,
            initSetUpState = initSetUpState
        )
    }
    else if (screenHeight in 300..600 && screenWidth in 700..1000) {
        DashBoardPhoneLandScape()
    }
    else if(screenHeight in 1000..1300 && screenWidth in 600 .. 1000) {
        DashBoardTabletPortrait(modifier)
    } else {
        DashBoardTabletLandScape(modifier)
    }
}






@Composable
fun SetUpDialog (
    fightToggleDialog: () -> Unit  = {},
    confirmBut: () ->Unit = {},
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
) {

    val spacing = LocalSpacing.current
    val fontSize = LocalFontSize.current

    Dialog(onDismissRequest = fightToggleDialog ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(spacing.medium)
            ) {
                DropDown(
                    setDurationMinutes = setDurationMinutes,
                    setRestDurationMinutes = setRestDurationMinutes,
                    setSessions = setSessions,
                    listFocusDuration = listFocusDuration,
                    listRestDuration = listRestDuration,
                    listSessions = listSessions,
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = fightToggleDialog
                ) { Text("Back") }
                Button(
                    onClick = confirmBut
                ) { Text("Start") }
            }
        }
    }
}

@Preview
@Composable
fun SetUpPopUpPreview () {
    MyAppTheme {
        Column() {
            if (true) {
                SetUpDialog()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardPhonePortrait (
    modifier: Modifier = Modifier,
    toggleSetUpPopup: () -> Unit = {},
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
    confirmBut: () -> Unit = {},
    initSetUpState: InitSetUpState
) {
    var expandedIndex: Int? by remember { mutableStateOf<Int?>(null) }

    val spacing = LocalSpacing.current
    val fontSize = LocalFontSize.current

    Modifier.padding(horizontal = spacing.medium)
    Modifier.padding(vertical = spacing.large)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val monsterList: List<Triple<Painter, String, String>> = listOf(
            Triple(
                painterResource(id = R.drawable._01_1),
                "Anxiety",
                "Makes everyday tasks feel overwhelming"
            ),
            Triple(
                painterResource(id = R.drawable._01_2),
                "Loneliness",
                "Leads to isolation and low self-worth"
            ),
            Triple(
                painterResource(id = R.drawable._02_2),
                "Burnout",
                "Kills motivation and joy in learning"
            ),
            Triple(
                painterResource(id = R.drawable._03_2),
                "Comparison",
                "Breeds insecurity through social media"
            ),
            Triple(
                painterResource(id = R.drawable._04_1),
                "Rejection",
                "Shakes confidence and self-image"
            ),
            Triple(
                painterResource(id = R.drawable._03_3),
                "Pressure",
                "Creates fear of failure and perfectionism"
            ),
            Triple(
                painterResource(id = R.drawable._06_2),
                "Procrastination",
                "Delays growth and builds guilt"
            ),
            Triple(
                painterResource(id = R.drawable._07_2),
                "Identity",
                "Confuses self-understanding and belonging"
            ),
            Triple(
                painterResource(id = R.drawable._08_2),
                "Addiction",
                "Distracts from goals and relationships"
            ),
            Triple(
                painterResource(id = R.drawable._09_2),
                "Bullying",
                "Damages trust and emotional safety"
            ),
            Triple(
                painterResource(id = R.drawable._10_2),
                "Self-Doubt",
                "Blocks ambition and creativity"
            ),
            Triple(
                painterResource(id = R.drawable._12_1),
                "Financial Stress",
                "Limits opportunity and causes anxiety"
            ),
            Triple(
                painterResource(id = R.drawable._07_3),
                "Overthinking",
                "Paralyzes decision-making"
            ),
            Triple(
                painterResource(id = R.drawable._13_2),
                "Imposter",
                "Makes success feel undeserved"
            ),
            Triple(
                painterResource(id = R.drawable._08_3),
                "Neglect",
                "Leaves emotional needs unmet"
            ),
            Triple(
                painterResource(id = R.drawable._11_1),
                "Fear",
                "Prevents risk-taking and growth"
            ),
            Triple(
                painterResource(id = R.drawable._14_1),
                "Toxic Positivity",
                "Invalidates real emotions"
            ),
            Triple(
                painterResource(id = R.drawable._15_1),
                "Distraction",
                "Scatters focus and productivity"
            ),
            Triple(
                painterResource(id = R.drawable._16_3),
                "Insecurity",
                "Erodes confidence and self-love"
            ),
            Triple(
                painterResource(id = R.drawable._14_3),
                "Perfectionism",
                "Turns effort into self-criticism"
            ),
            Triple(
                painterResource(id = R.drawable._18_2),
                "Isolation",
                "Disconnects from support systems"
            ),
            Triple(
                painterResource(id = R.drawable._19_2),
                "Uncertainty",
                "Creates anxiety about the future"
            ),
            Triple(painterResource(id = R.drawable._20_1), "Regret", "Chains you to the past")
        )

        BoxWithConstraints {
            val maxHeight = this.maxHeight
            val maxWidth = this.maxWidth


            val adaptivePaddingHorizontal = maxWidth * 0.05f
            val adaptivePaddingVertical: Dp = maxHeight * 0.1f


            val estimatedColumns = 2 // or calculate based on screen width
            Log.d(
                "DEBUG",
                "screenWidth/estimatedColumns: ${maxWidth / estimatedColumns}, screenHeight: $maxHeight"
            )
            //screenWidth/estimatedColumns: 205.7.dp  - screenHeight = 776.dp

            val minCellSize = maxOf(70.dp, minOf(90.dp, maxWidth / estimatedColumns))

            //Monster info panel + Monster pick side
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = adaptivePaddingHorizontal,
                        vertical = adaptivePaddingVertical
                    ),
                elevation = CardDefaults.cardElevation(16.dp),
                colors = CardDefaults.cardColors(Color(0xFF7A490C))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = spacing.medium,
                            start = spacing.medium,
                            end = spacing.medium,
                            bottom = spacing.small
                        )
                        .height(maxHeight * 0.2f),
                    colors = CardDefaults.cardColors(Color(0xFFCCC127)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(spacing.small),
                    ) {
                        Row() {
                            Image(
                                painter = monsterList.getOrNull(expandedIndex ?: 0)?.first
                                    ?: painterResource(id = R.drawable._01_1),
                                contentDescription = null,
                            )
                            Text(
                                text = monsterList.getOrNull(expandedIndex ?: 0)?.second
                                    ?: "Anxiety",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = monsterList.getOrNull(expandedIndex ?: 0)?.third
                                ?: "Makes everyday tasks feel overwhelming",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    text = "Total: ${monsterList.size} monsters",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = spacing.medium),
                    color = Color.White
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = minCellSize),
                    modifier = Modifier
                        .height(maxHeight * 0.7f)
                        .padding(bottom = spacing.medium),
                    contentPadding = PaddingValues(
                        start = spacing.medium,
                        end = spacing.medium,
                        bottom = spacing.medium,
                        top = spacing.small
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    items(monsterList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    expandedIndex = if (expandedIndex == index) null else index
                                },
                            colors = if (expandedIndex == index) CardDefaults.cardColors(Color.LightGray) else CardDefaults.cardColors(
                                Color(0xFFCCC127)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Image(
                                    painter = monsterList[index].first,
                                    contentDescription = null,
                                    modifier = Modifier.size(minCellSize * 0.8f)
                                )
                                Text(
                                    text = monsterList[index].second,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            //Fight Button
            FightButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                maxWidth = maxWidth * 0.25f,
                maxHeight = maxHeight * 0.05f,
                fontSize = fontSize.large,
                toggleSetUpPopup = toggleSetUpPopup
            )
            if (initSetUpState.toggleSetUp) {
                SetUpDialog(
                    fightToggleDialog = toggleSetUpPopup,
                    setDurationMinutes = setDurationMinutes,
                    setRestDurationMinutes = setRestDurationMinutes,
                    setSessions = setSessions,
                    listFocusDuration = listFocusDuration,
                    listRestDuration = listRestDuration,
                    listSessions = listSessions,
                    confirmBut = confirmBut,
                )
            }
        }
    }
}




@Composable
fun DashBoardPhoneLandScape (
    modifier: Modifier = Modifier
) {
    val spacing: Spacing = LocalSpacing.current
    val fontSize: FontSize = LocalFontSize.current
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val monsterList: List<Triple<Painter, String, String>> = listOf(
            Triple(
                painterResource(id = R.drawable._01_1),
                "Anxiety",
                "Makes everyday tasks feel overwhelming"
            ),
            Triple(
                painterResource(id = R.drawable._01_2),
                "Loneliness",
                "Leads to isolation and low self-worth"
            ),
            Triple(
                painterResource(id = R.drawable._02_2),
                "Burnout",
                "Kills motivation and joy in learning"
            ),
            Triple(
                painterResource(id = R.drawable._03_2),
                "Comparison",
                "Breeds insecurity through social media"
            ),
            Triple(
                painterResource(id = R.drawable._04_1),
                "Rejection",
                "Shakes confidence and self-image"
            ),
            Triple(
                painterResource(id = R.drawable._03_3),
                "Pressure",
                "Creates fear of failure and perfectionism"
            ),
            Triple(
                painterResource(id = R.drawable._06_2),
                "Procrastination",
                "Delays growth and builds guilt"
            ),
            Triple(
                painterResource(id = R.drawable._07_2),
                "Identity",
                "Confuses self-understanding and belonging"
            ),
            Triple(
                painterResource(id = R.drawable._08_2),
                "Addiction",
                "Distracts from goals and relationships"
            ),
            Triple(
                painterResource(id = R.drawable._09_2),
                "Bullying",
                "Damages trust and emotional safety"
            ),
            Triple(
                painterResource(id = R.drawable._10_2),
                "Self-Doubt",
                "Blocks ambition and creativity"
            ),
            Triple(
                painterResource(id = R.drawable._12_1),
                "Financial Stress",
                "Limits opportunity and causes anxiety"
            ),
            Triple(
                painterResource(id = R.drawable._07_3),
                "Overthinking",
                "Paralyzes decision-making"
            ),
            Triple(
                painterResource(id = R.drawable._13_2),
                "Imposter",
                "Makes success feel undeserved"
            ),
            Triple(
                painterResource(id = R.drawable._08_3),
                "Neglect",
                "Leaves emotional needs unmet"
            ),
            Triple(
                painterResource(id = R.drawable._11_1),
                "Fear",
                "Prevents risk-taking and growth"
            ),
            Triple(
                painterResource(id = R.drawable._14_1),
                "Toxic Positivity",
                "Invalidates real emotions"
            ),
            Triple(
                painterResource(id = R.drawable._15_1),
                "Distraction",
                "Scatters focus and productivity"
            ),
            Triple(
                painterResource(id = R.drawable._16_3),
                "Insecurity",
                "Erodes confidence and self-love"
            ),
            Triple(
                painterResource(id = R.drawable._14_3),
                "Perfectionism",
                "Turns effort into self-criticism"
            ),
            Triple(
                painterResource(id = R.drawable._18_2),
                "Isolation",
                "Disconnects from support systems"
            ),
            Triple(
                painterResource(id = R.drawable._19_2),
                "Uncertainty",
                "Creates anxiety about the future"
            ),
            Triple(painterResource(id = R.drawable._20_1), "Regret", "Chains you to the past")
        )
        BoxWithConstraints{
            val maxHeight = this.maxHeight
            val maxWidth = this.maxWidth

            val estimatedColumns = 2 // or calculate based on screen width
            Log.d("DEBUG", "screenWidth/estimatedColumns: ${maxWidth / estimatedColumns}, screenHeight: $maxHeight")
            //screenWidth/estimatedColumns: 205.7.dp  - screenHeight = 776.dp

            val minCellSize = maxOf(70.dp, minOf(90.dp, maxWidth / estimatedColumns))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.medium,
                            vertical = spacing.medium
                        ).weight(0.8f),
                    elevation = CardDefaults.cardElevation(16.dp),
                    colors = CardDefaults.cardColors(Color(0xFF7A490C))
                ) {
                    Row {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minCellSize),
                            modifier = Modifier
                                .padding(
                                    bottom = spacing.medium,
                                    top = spacing.medium,
                                )
                                .fillMaxWidth(0.7f),

                            contentPadding = PaddingValues(top = spacing.medium, start = spacing.medium, bottom = spacing.medium),

                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                        ) {
                            items(monsterList.size) { index ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color = Color.DarkGray)
                                        .clickable(onClick = {
                                            expandedIndex =
                                                if (expandedIndex == index) null else index
                                        }),
                                    colors = if (expandedIndex == index) CardDefaults.cardColors(
                                        Color.LightGray
                                    ) else CardDefaults.cardColors(Color(0xFFCCC127))
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Image(
                                            painter = monsterList[index].first,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(minCellSize * 0.8f)
                                        )
                                        Text(
                                            text = monsterList[index].second,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.padding(top = spacing.medium),
                        ) {
                            Text(
                                text = "Total: ${monsterList.size} monsters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = spacing.medium)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .padding(
                                        top = spacing.small,
                                        start = spacing.medium,
                                        end = spacing.medium,
                                        bottom = spacing.medium
                                    ),
                                colors = CardDefaults.cardColors(Color(0xFFCCC127))
                            ) {
                                Row(
                                    modifier = Modifier.padding(spacing.small)
                                ) {
                                    Image(
                                        painter = monsterList.getOrNull(expandedIndex ?: 0)?.first
                                            ?: painterResource(id = R.drawable._01_1),
                                        contentDescription = null,
                                    )
                                    Text(
                                        text = monsterList.getOrNull(expandedIndex ?: 0)?.second
                                            ?: "Anxiety",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = monsterList.getOrNull(expandedIndex ?: 0)?.third
                                        ?: "Makes everyday tasks feel overwhelming",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(spacing.small)
                                )
                            }
                        }
                    }
                }
                FightButton(
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(spacing.medium),
                    maxWidth = maxWidth * 0.09f,
                    maxHeight = maxHeight * 0.1f,
                    fontSize = fontSize.medium
                )

                if (initSetUpState.toggleSetUp) {
                    SetUpDialog(
                        fightToggleDialog = toggleSetUpPopup,
                        setDurationMinutes = setDurationMinutes,
                        setRestDurationMinutes = setRestDurationMinutes,
                        setSessions = setSessions,
                        listFocusDuration = listFocusDuration,
                        listRestDuration = listRestDuration,
                        listSessions = listSessions,
                        confirmBut = confirmBut,
                    )
                }
            }
        }
    }
}


@Composable
fun DashBoardTabletPortrait (
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val fontSize = LocalFontSize.current

    Modifier.padding(horizontal = spacing.medium)
    Modifier.padding(vertical = spacing.large)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val monsterList: List<Triple<Painter, String, String>> = listOf(
            Triple(painterResource(id = R.drawable._01_1), "Anxiety", "Makes everyday tasks feel overwhelming"),
            Triple(painterResource(id = R.drawable._01_2), "Loneliness", "Leads to isolation and low self-worth"),
            Triple(painterResource(id = R.drawable._02_2), "Burnout", "Kills motivation and joy in learning"),
            Triple(painterResource(id = R.drawable._03_2), "Comparison", "Breeds insecurity through social media"),
            Triple(painterResource(id = R.drawable._04_1), "Rejection", "Shakes confidence and self-image"),
            Triple(painterResource(id = R.drawable._03_3), "Pressure", "Creates fear of failure and perfectionism"),
            Triple(painterResource(id = R.drawable._06_2), "Procrastination", "Delays growth and builds guilt"),
            Triple(painterResource(id = R.drawable._07_2), "Identity", "Confuses self-understanding and belonging"),
            Triple(painterResource(id = R.drawable._08_2), "Addiction", "Distracts from goals and relationships"),
            Triple(painterResource(id = R.drawable._09_2), "Bullying", "Damages trust and emotional safety"),
            Triple(painterResource(id = R.drawable._10_2), "Self-Doubt", "Blocks ambition and creativity"),
            Triple(painterResource(id = R.drawable._12_1), "Financial Stress", "Limits opportunity and causes anxiety"),
            Triple(painterResource(id = R.drawable._07_3), "Overthinking", "Paralyzes decision-making"),
            Triple(painterResource(id = R.drawable._13_2), "Imposter", "Makes success feel undeserved"),
            Triple(painterResource(id = R.drawable._08_3), "Neglect", "Leaves emotional needs unmet"),
            Triple(painterResource(id = R.drawable._11_1), "Fear", "Prevents risk-taking and growth"),
            Triple(painterResource(id = R.drawable._14_1), "Toxic Positivity", "Invalidates real emotions"),
            Triple(painterResource(id = R.drawable._15_1), "Distraction", "Scatters focus and productivity"),
            Triple(painterResource(id = R.drawable._16_3), "Insecurity", "Erodes confidence and self-love"),
            Triple(painterResource(id = R.drawable._14_3), "Perfectionism", "Turns effort into self-criticism"),
            Triple(painterResource(id = R.drawable._18_2), "Isolation", "Disconnects from support systems"),
            Triple(painterResource(id = R.drawable._19_2), "Uncertainty", "Creates anxiety about the future"),
            Triple(painterResource(id = R.drawable._20_1), "Regret", "Chains you to the past")
        )
        var expandedIndex by remember { mutableStateOf<Int?>(null) }

        BoxWithConstraints(){
            val maxHeight = this.maxHeight
            val maxWidth = this.maxWidth
            val adaptivePaddingHorizontal = maxWidth * 0.05f
            val adaptivePaddingVertical: Dp = maxHeight * 0.1f


            val estimatedColumns = 2 // or calculate based on screen width
            Log.d("DEBUG", "screenWidth/estimatedColumns: ${maxWidth/estimatedColumns}, screenHeight: $maxHeight")
            //screenWidth/estimatedColumns: 205.7.dp  - screenHeight = 776.dp

            val minCellSize = maxOf(70.dp, minOf(120.dp, maxWidth / estimatedColumns))

            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = adaptivePaddingHorizontal,
                        vertical = adaptivePaddingVertical
                    ),
                elevation = CardDefaults.cardElevation(16.dp),
                colors = CardDefaults.cardColors(Color(0xFF7A490C))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.medium, start = spacing.medium, end = spacing.medium, bottom = spacing.small)
                        .height(maxHeight * 0.2f),
                    colors = CardDefaults.cardColors(Color(0xFFCCC127)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(spacing.small),
                    ) {
                        Row() {
                            Image(
                                painter = monsterList.getOrNull(expandedIndex ?: 0)?.first
                                    ?: painterResource(id = R.drawable._01_1),
                                contentDescription = null,
                                modifier = Modifier.size(minCellSize * 0.8f)
                            )
                            Text(
                                text = monsterList.getOrNull(expandedIndex ?: 0)?.second
                                    ?: "Anxiety",
                                fontSize = fontSize.large,
                                fontFamily =  FontFamily(Font(R.font.jersey))
                            )
                        }
                        Text(
                            text = monsterList.getOrNull(expandedIndex ?: 0)?.third
                                ?: "Makes everyday tasks feel overwhelming",
                            fontSize = fontSize.large,
                            fontFamily =  FontFamily(Font(R.font.jersey))
                        )
                    }
                }
                Text(
                    text = "Total: ${monsterList.size} monsters",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = spacing.medium),
                    color = Color.White
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minCellSize ),
                    modifier = Modifier
                        .padding(bottom = spacing.medium)
                        .fillMaxWidth()
                        .height(maxHeight * 0.7f),

                    contentPadding = PaddingValues(start = spacing.medium, end = spacing.medium, bottom = spacing.medium, top = spacing.small),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    items(monsterList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = {
                                    expandedIndex = if (expandedIndex == index) null else index
                                }),
                            colors = if(expandedIndex == index) CardDefaults.cardColors(Color.LightGray) else CardDefaults.cardColors(Color(0xFFCCC127))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Image(
                                    painter = monsterList[index].first,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(minCellSize* 0.8f)
                                        .padding(top = spacing.small),
                                )
                                Text(
                                    text = monsterList[index].second,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily(Font(R.font.jersey))
                                )
                            }
                        }
                    }
                }
            }
            FightButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                maxWidth = maxWidth * 0.25f,
                maxHeight = maxHeight * 0.05f,
                fontSize = fontSize.large
            )
        }
    }
}

@Composable
fun DashBoardTabletLandScape (
    modifier: Modifier = Modifier
) {
    val spacing: Spacing = LocalSpacing.current
    val fontSize: FontSize = LocalFontSize.current
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val monsterList: List<Triple<Painter, String, String>> = listOf(
            Triple(
                painterResource(id = R.drawable._01_1),
                "Anxiety",
                "Makes everyday tasks feel overwhelming"
            ),
            Triple(
                painterResource(id = R.drawable._01_2),
                "Loneliness",
                "Leads to isolation and low self-worth"
            ),
            Triple(
                painterResource(id = R.drawable._02_2),
                "Burnout",
                "Kills motivation and joy in learning"
            ),
            Triple(
                painterResource(id = R.drawable._03_2),
                "Comparison",
                "Breeds insecurity through social media"
            ),
            Triple(
                painterResource(id = R.drawable._04_1),
                "Rejection",
                "Shakes confidence and self-image"
            ),
            Triple(
                painterResource(id = R.drawable._03_3),
                "Pressure",
                "Creates fear of failure and perfectionism"
            ),
            Triple(
                painterResource(id = R.drawable._06_2),
                "Procrastination",
                "Delays growth and builds guilt"
            ),
            Triple(
                painterResource(id = R.drawable._07_2),
                "Identity",
                "Confuses self-understanding and belonging"
            ),
            Triple(
                painterResource(id = R.drawable._08_2),
                "Addiction",
                "Distracts from goals and relationships"
            ),
            Triple(
                painterResource(id = R.drawable._09_2),
                "Bullying",
                "Damages trust and emotional safety"
            ),
            Triple(
                painterResource(id = R.drawable._10_2),
                "Self-Doubt",
                "Blocks ambition and creativity"
            ),
            Triple(
                painterResource(id = R.drawable._12_1),
                "Financial Stress",
                "Limits opportunity and causes anxiety"
            ),
            Triple(
                painterResource(id = R.drawable._07_3),
                "Overthinking",
                "Paralyzes decision-making"
            ),
            Triple(
                painterResource(id = R.drawable._13_2),
                "Imposter",
                "Makes success feel undeserved"
            ),
            Triple(
                painterResource(id = R.drawable._08_3),
                "Neglect",
                "Leaves emotional needs unmet"
            ),
            Triple(
                painterResource(id = R.drawable._11_1),
                "Fear",
                "Prevents risk-taking and growth"
            ),
            Triple(
                painterResource(id = R.drawable._14_1),
                "Toxic Positivity",
                "Invalidates real emotions"
            ),
            Triple(
                painterResource(id = R.drawable._15_1),
                "Distraction",
                "Scatters focus and productivity"
            ),
            Triple(
                painterResource(id = R.drawable._16_3),
                "Insecurity",
                "Erodes confidence and self-love"
            ),
            Triple(
                painterResource(id = R.drawable._14_3),
                "Perfectionism",
                "Turns effort into self-criticism"
            ),
            Triple(
                painterResource(id = R.drawable._18_2),
                "Isolation",
                "Disconnects from support systems"
            ),
            Triple(
                painterResource(id = R.drawable._19_2),
                "Uncertainty",
                "Creates anxiety about the future"
            ),
            Triple(painterResource(id = R.drawable._20_1), "Regret", "Chains you to the past")
        )
        BoxWithConstraints() {
            val maxHeight = this.maxHeight
            val maxWidth = this.maxWidth

            val estimatedColumns = 2 // or calculate based on screen width
            Log.d(
                "DEBUG",
                "screenWidth/estimatedColumns: ${maxWidth / estimatedColumns}, screenHeight: $maxHeight"
            )
            //screenWidth/estimatedColumns: 205.7.dp  - screenHeight = 776.dp

            val minCellSize = maxOf(70.dp, minOf(120.dp, maxWidth / estimatedColumns))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.medium,
                            vertical = spacing.medium
                        ).weight(0.8f),
                    elevation = CardDefaults.cardElevation(16.dp),
                    colors = CardDefaults.cardColors(Color(0xFF7A490C))
                ) {
                    Row {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minCellSize),
                            modifier = Modifier
                                .padding(
                                    bottom = spacing.medium,
                                    top = spacing.medium,
                                )
                                .fillMaxWidth(0.7f),

                            contentPadding = PaddingValues(top = spacing.medium, start = spacing.medium, bottom = spacing.medium),

                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                        ) {
                            items(monsterList.size) { index ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color = Color.DarkGray)
                                        .clickable(onClick = {
                                            expandedIndex =
                                                if (expandedIndex == index) null else index
                                        }),
                                    colors = if (expandedIndex == index) CardDefaults.cardColors(
                                        Color.LightGray
                                    ) else CardDefaults.cardColors(Color(0xFFCCC127))
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Image(
                                            painter = monsterList[index].first,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(minCellSize * 0.8f)
                                        )
                                        Text(
                                            text = monsterList[index].second,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.padding(top = spacing.medium),
                        ) {
                            Text(
                                text = "Total: ${monsterList.size} monsters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = spacing.medium)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .padding(
                                        top = spacing.small,
                                        start = spacing.medium,
                                        end = spacing.medium,
                                        bottom = spacing.medium
                                    ),
                                colors = CardDefaults.cardColors(Color(0xFFCCC127))
                            ) {
                                Row(
                                    modifier = Modifier.padding(spacing.small)
                                ) {
                                    Image(
                                        painter = monsterList.getOrNull(expandedIndex ?: 0)?.first
                                            ?: painterResource(id = R.drawable._01_1),
                                        contentDescription = null,
                                    )
                                    Text(
                                        text = monsterList.getOrNull(expandedIndex ?: 0)?.second
                                            ?: "Anxiety",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = monsterList.getOrNull(expandedIndex ?: 0)?.third
                                        ?: "Makes everyday tasks feel overwhelming",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(spacing.small)
                                )
                            }
                        }
                    }
                }
                FightButton(
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(spacing.medium),
                    maxWidth = maxWidth * 0.09f,
                    maxHeight = maxHeight * 0.1f,
                    fontSize = fontSize.medium
                )
            }
        }
    }
}


@Composable
fun FightButton (
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp,
    fontSize: TextUnit,
    toggleSetUpPopup: () -> Unit = {}
) {
    Button(
        onClick = toggleSetUpPopup ,
        modifier = modifier
            .padding(top = adaptivePadding())
            .size(width = maxWidth, height = maxHeight)
    ) {
        Text(
            text = "Fight",
            fontSize = fontSize
        )
    }
}


@Composable
fun adaptivePadding(): Dp {
    val configuration = LocalWindowInfo.current
    val screenWidth = configuration.containerSize.width.dp

    return maxOf(16.dp, minOf(screenWidth * 0.05f, 48.dp))
}

@Composable
fun adaptiveFontSize(): TextUnit {
    val configuration = LocalWindowInfo.current
    val screenWidth = configuration.containerSize.width
    val screenHeight = configuration.containerSize.height
    return when {
        screenWidth < 400  -> 14.sp
        screenWidth < 600 -> 16.sp
        else -> 25.sp
    }
}





@Preview(widthDp = 393, heightDp = 851, showBackground = true)
@Composable
fun PreviewPhonePortrait () {
    MyAppTheme {
        DashBoardPhonePortrait(initSetUpState = InitSetUpState())
    }
}


@Preview(widthDp = 851, heightDp = 393, showBackground = true)
@Composable
fun PreviewPhoneLandscape () {
    MyAppTheme {
        DashBoardPhoneLandScape()
    }
}



@Preview(widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
fun PreviewPortraitTablet () {
    MyAppTheme {
        DashBoardTabletPortrait()
    }
}

@Preview(widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun PreviewLandscapeTablet () {
    MyAppTheme {
        DashBoardTabletLandScape()
    }
}