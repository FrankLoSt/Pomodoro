package com.example.pomodoro.ui.countdown


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pomodoro.R
import com.example.pomodoro.ui.pickmonster.LocalFontSize
import com.example.pomodoro.ui.pickmonster.LocalSpacing
import com.example.pomodoro.ui.pickmonster.MyAppTheme
import com.example.pomodoro.ui.pickmonster.Spacing
import com.example.pomodoro.ui.theme.PomodoroTheme


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
    windowSizeClass: WindowSizeClass?
) {
    Dialog(
        onDismissRequest = fightToggleDialog,

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





@Composable
fun DropDownPortrait (
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
    windowSizeClass: WindowSizeClass?,
    fightToggleDialog: () -> Unit = {},
    confirmBut: () -> Unit = {}
) {
    val spacing: Spacing = LocalSpacing.current
    val maxWidth = LocalWindowInfo.current.containerSize.width
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .width(
                    if (windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Compact) (maxWidth * 0.8f).dp
                    else (maxWidth * 0.5f).dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            top = spacing.medium,
                            bottom = spacing.small
                        ).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.focus_duration),
                            style = MaterialTheme.typography.titleMedium
                        )
                        DropdownFun(
                            itemLists = listFocusDuration,
                            onItemSelected = { minutes ->
                                setDurationMinutes(minutes)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.padding(
                            top = spacing.medium,
                            bottom = spacing.small
                        ).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.rest_duration),
                            style = MaterialTheme.typography.titleMedium
                        )
                        DropdownFun(
                            itemLists = listRestDuration,
                            onItemSelected = { minutes ->
                                setRestDurationMinutes(minutes)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.padding(
                            top = spacing.medium,
                            bottom = spacing.small
                        ).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.sessions),
                            style = MaterialTheme.typography.titleMedium
                        )
                        DropdownSessionFun(
                            itemLists = listSessions,
                            onItemSelected = { sessions ->
                                setSessions(sessions)
                            }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
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
    }
}




@Preview(
    name = "Expanded Landscape",
    widthDp = 915,
    heightDp = 412,
    showBackground = true
)
@Composable()
fun DropDownPreview () {
    DropDownPortrait(
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
        windowSizeClass = null
    )
}

@Preview(
    showBackground = true,
    name = "Expanded Portrait",
    widthDp = 412,
    heightDp = 915
)
@Composable
fun DropDownPreview2 () {
    DropDownPortrait(
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
        windowSizeClass = null
    )
}



@Composable
fun DropdownFun (
    itemLists: List<Int>,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedDuration by rememberSaveable { mutableIntStateOf(itemLists[0]) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .width(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
            Text(
                text = if (selectedDuration == 1) "$selectedDuration min" else "$selectedDuration mins",
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp) // limit height
        ) {
            itemLists.forEach { duration ->
                DropdownMenuItem(
                    text = {
                        Text(text = "$duration mins")
                    },
                    onClick = {
                        onItemSelected(duration)
                        selectedDuration = duration
                        expanded = false
                    }
                )
            }
        }
    }
}







@Composable
fun DropdownSessionFun(
    itemLists: List<Int>,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableIntStateOf(itemLists[0]) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .width(150.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = if (selectedItem == 1) "$selectedItem session" else "$selectedItem sessions",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
            )
        }
        // ⬇️ Important: DropdownMenu is OUTSIDE Row but still inside Box
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            itemLists.forEach { session ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (session == 1) "$session session" else "$session sessions",
                        )
                           },
                    onClick = {
                        onItemSelected(session)
                        selectedItem = session
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun DropdownMonsterFun(
    itemLists: List<String>,
    onItemSelected: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableStateOf(itemLists[0]) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .width(150.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "$selectedItem monster",
                modifier = Modifier.padding(8.dp).weight(0.5f),
                style = MaterialTheme.typography.titleSmall,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,

            )
        }
        // ⬇️ Important: DropdownMenu is OUTSIDE Row but still inside Box
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            itemLists.forEach { monster ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = monster,
                        )
                    },
                    onClick = {
                        onItemSelected(monster)
                        selectedItem = monster
                        expanded = false
                    }
                )
            }
        }
    }
}







@Preview
@Composable
fun AlertDialog1Preview () {
    PomodoroTheme {
        AlertDialog1(
            onDismiss = {},
            text1 = R.string.congrat_mess,
            text2 = R.string.you_ve_earned_an_armor,
            text3 = R.string.you_ve_earned_an_armor,
            image = R.drawable.amor,
        )
    }
}




