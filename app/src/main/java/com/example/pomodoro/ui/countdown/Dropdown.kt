package com.example.pomodoro.ui.countdown


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pomodoro.R
import com.example.pomodoro.ui.pickmonster.LocalSpacing
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
            colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        spacing.small
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
                    modifier = Modifier.padding(spacing.small).fillMaxWidth(),
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
                        start = spacing.small,
                        end = spacing.small,
                        top = spacing.small
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
                LongBreakSetting(
                    longBreakList = listRestDuration,
                    sessionList = listSessions,
                    onLongBreakSelected = {},
                    onSessionSelected = {}

                    )
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



@Composable
fun SettingsCard(
    windowSizeClass: WindowSizeClass?,
    listFocusDuration: List<Int>,
    listRestDuration: List<Int>,
    listSessions: List<Int>,
    setDurationMinutes: (Int) -> Unit,
    setRestDurationMinutes: (Int) -> Unit,
    setSessions: (Int) -> Unit,
    fightToggleDialog: () -> Unit,
    confirmBut: () -> Unit,
) {
    val spacing: Spacing = LocalSpacing.current
    val maxWidth = LocalWindowInfo.current.containerSize.width

    Column(
        modifier = Modifier
            .width(
                if (windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Compact)
                    (maxWidth * 0.85f).dp
                else (maxWidth * 0.5f).dp
            )
            .padding(vertical = spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding( top = 16.dp, bottom = 16.dp, start = 20.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {

                // --- Focus Duration ---
                SettingRow(
                    label = stringResource(R.string.focus_duration),
                    content = {
                        DropdownFun(
                            itemLists = listFocusDuration,
                            onItemSelected = setDurationMinutes
                        )
                    }
                )

                // --- Rest Duration ---
                SettingRow(
                    label = stringResource(R.string.rest_duration),
                    content = {
                        DropdownFun(
                            itemLists = listRestDuration,
                            onItemSelected = setRestDurationMinutes
                        )
                    }
                )

                // --- Sessions ---
                SettingRow(
                    label = stringResource(R.string.sessions),
                    content = {
                        DropdownSessionFun(
                            itemLists = listSessions,
                            onItemSelected = setSessions
                        )
                    }
                )

                // --- Long Break ---
                Box(
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                LongBreakSetting(
                    longBreakList = listRestDuration,
                    sessionList = listSessions,
                    onLongBreakSelected = {},
                    onSessionSelected = {}
                )
                }

                // --- Buttons ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.medium)
                ) {
                    Button(
                        onClick = fightToggleDialog,
                        modifier = Modifier.weight(1f)
                    ) { Text("Back") }

                    Button(
                        onClick = confirmBut,
                        modifier = Modifier.weight(1f)
                    ) { Text("Start") }
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            content()
        }
    }
}




@Composable
fun LongBreak () {

}


@Preview(
    name = "Expanded Landscape",
    widthDp = 915,
    heightDp = 412,
    showBackground = true
)
@Composable()
fun DropDownPreview () {
    SettingsCard(
        windowSizeClass = null,
        listFocusDuration = listOf(1,2,3,4,5),
        listRestDuration = listOf(1,2,3,4,5),
        listSessions = listOf(1,2,3,4,5),
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
        fightToggleDialog = {},
        confirmBut = {}
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
    SettingsCard(
        windowSizeClass = null,
        listFocusDuration = listOf(1,2,3,4,5),
        listRestDuration = listOf(1,2,3,4,5),
        listSessions = listOf(1,2,3,4,5),
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
        fightToggleDialog = {},
        confirmBut = {}
    )
}



@Composable
fun DropdownFun1 (
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
fun DropdownFun(
    itemLists: List<Int> = listOf(1,2,3,4,5,6,7,8,89,132),
    onItemSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedDuration by rememberSaveable { mutableIntStateOf(itemLists[0]) }

    // Use a key to measure width of parent box to match dropdown
    var parentWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp)) // 1️⃣ Clip the entire composable first
            .background(Color.White, RoundedCornerShape(12.dp)) // 2️⃣ Then apply background *inside the clip*
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates ->
                parentWidth = coordinates.size.width
            }.widthIn(min = 150.dp, max = 170.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .width(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedDuration == 1) "$selectedDuration min" else "$selectedDuration mins",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentWidth.toDp() }) // Match width
                .heightIn(max = 200.dp)
                .background(Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            itemLists.forEach { duration ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (duration == 1) "$duration min" else "$duration mins",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        selectedDuration = duration
                        expanded = false
                        onItemSelected(duration)
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

    var parentWidth by rememberSaveable { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates ->
                parentWidth = coordinates.size.width
            }.widthIn(min = 150.dp, max = 170.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .width(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedItem == 1) "$selectedItem session" else "$selectedItem sessions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 1
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // ⬇️ Important: DropdownMenu is OUTSIDE Row but still inside Box
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentWidth.toDp() }) // Match width
                .heightIn(max = 200.dp)
                .background(Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            itemLists.forEach { session ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (session == 1) "$session session" else "$session sessions",
                            style = MaterialTheme.typography.bodyMedium
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
fun LongBreakSetting(
    longBreakList: List<Int> = listOf(5, 10, 15, 20),
    sessionList: List<Int> = listOf(2, 3, 4, 5),
    onLongBreakSelected: (Int) -> Unit,
    onSessionSelected: (Int) -> Unit,
) {
    var expandedBreak by rememberSaveable { mutableStateOf(false) }
    var expandedSession by rememberSaveable { mutableStateOf(false) }

    var selectedBreak by rememberSaveable { mutableIntStateOf(longBreakList.first()) }
    var selectedSession by rememberSaveable { mutableIntStateOf(sessionList.first()) }

    var parentWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates ->
                parentWidth = coordinates.size.width
            }.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.Center).padding(start = 8.dp)
        ) {
            // --- First Dropdown (Long break duration) ---
            Text(text = "Long break", style = MaterialTheme.typography.bodyMedium)

            Box {
                Row(
                    modifier = Modifier
                        .clickable { expandedBreak = !expandedBreak }
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedBreak mins",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = if (expandedBreak) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = expandedBreak,
                    onDismissRequest = { expandedBreak = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { parentWidth.toDp() / 3 })
                        .heightIn(max = 200.dp)
                        .background(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    longBreakList.forEach { duration ->
                        DropdownMenuItem(
                            text = { Text("$duration mins") },
                            onClick = {
                                selectedBreak = duration
                                expandedBreak = false
                                onLongBreakSelected(duration)
                            }
                        )
                    }
                }
            }

            // --- Static text between dropdowns ---
            Text(text = "After", style = MaterialTheme.typography.bodyMedium)

            // --- Second Dropdown (Session count) ---
            Box {
                Row(
                    modifier = Modifier
                        .clickable { expandedSession = !expandedSession }
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedSession sessions",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = if (expandedSession) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = expandedSession,
                    onDismissRequest = { expandedSession = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { parentWidth.toDp() / 3 })
                        .heightIn(max = 200.dp)
                        .background(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    sessionList.forEach { session ->
                        DropdownMenuItem(
                            text = { Text("$session sessions") },
                            onClick = {
                                selectedSession = session
                                expandedSession = false
                                onSessionSelected(session)
                            }
                        )
                    }
                }
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




