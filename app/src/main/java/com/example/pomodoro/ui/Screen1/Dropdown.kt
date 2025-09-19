package com.example.pomodoro.ui.Screen1


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.R
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState





@Composable
fun DropDown (
    viewModel: ViewModelCountDown,
    focusUiState: FocusUiState,
    restUiState: RestUiState,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(R.drawable.panel_rec),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(370.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(dimensionResource(R.dimen.medium_padding))
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
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
            Column() {
                DropdownFun(
                    itemLists = focusUiState.listFocusDuration,
                    onItemSelected = { minutes ->
                        viewModel.setDurationMinutes(minutes)
                    }
                )
                DropdownFun(
                    itemLists = restUiState.listRestDuration,
                    onItemSelected = { minutes ->
                        viewModel.setRestDurationMinutes(minutes)
                    }
                )
                DropdownSessionFun(
                    itemLists = focusUiState.listSessions,
                    onItemSelected = { sessions ->
                        viewModel.setSessions(sessions)
                    }
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DropDownPreview () {
    DropDown(
        focusUiState = FocusUiState(),
        restUiState = RestUiState(),
        viewModel = viewModel()
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
            modifier = Modifier.clickable { expanded = true }.width(120.dp),
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
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
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
                .width(120.dp)
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
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
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
fun AlertDialog1 (
    onDismiss: () -> Unit,
    focusUiState: FocusUiState,
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
                Text(
                    text = "Hooray! ",
                    style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "you've spent ${focusUiState.duration} minutes doing something meaningful with your life!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "you've earned 15 lucky coins!",
                )
                Image(
                    painter = painterResource(R.drawable.golden_coin_with_clover_icon),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Button(onClick = onDismiss) {
                    Text("Okay")
                }
            }
        }
    }
}









