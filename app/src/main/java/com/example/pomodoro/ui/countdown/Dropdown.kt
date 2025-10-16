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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pomodoro.R
import com.example.pomodoro.ui.theme.PomodoroTheme


@Composable
fun DropDown (
    setDurationMinutes: (Int) -> Unit = {},
    setRestDurationMinutes: (Int) -> Unit = {},
    setSessions: (Int) -> Unit = {},
    listFocusDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listRestDuration: List<Int> = listOf(1, 2, 3, 4, 5),
    listSessions: List<Int> = listOf(1, 2, 3, 4, 5),
) {
    BoxWithConstraints {
        val maxWidth = this.maxWidth

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .size(360.dp)
                    .padding(dimensionResource(R.dimen.medium_padding)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(45.dp)
                ) {
                    Text(
                        text = stringResource(R.string.focus_duration),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.rest_duration),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.sessions),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    DropdownFun(
                        itemLists = listFocusDuration,
                        onItemSelected = { minutes ->
                            setDurationMinutes(minutes)
                        }
                    )
                    DropdownFun(
                        itemLists = listRestDuration,
                        onItemSelected = { minutes ->
                            setRestDurationMinutes(minutes)
                        }
                    )
                    DropdownSessionFun(
                        itemLists = listSessions,
                        onItemSelected = { sessions ->
                            setSessions(sessions)
                        }
                    )
                }
            }
        }
    }
}




@Preview(
    name = "Expanded Landscape",
    widthDp = 800,
    heightDp = 400,
    showBackground = true
)
@Composable
fun DropDownPreview () {
    DropDown(
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
    )
}

@Preview(showBackground = true)
@Composable
fun DropDownPreview2 () {
    DropDown(
        setDurationMinutes = {},
        setRestDurationMinutes = {},
        setSessions = {},
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




