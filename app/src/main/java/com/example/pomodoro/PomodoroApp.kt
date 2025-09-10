package com.example.pomodoro


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CounDownTimer (
    viewModel: ViewModelCounDown = viewModel(),
) {
     Column(
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally,
         modifier = Modifier
             .fillMaxSize()
             .padding(top = 100.dp)
     ) {
         Text(
             text = viewModel.countdown,
             style = MaterialTheme.typography.displayLarge
         )
         Row(
             horizontalArrangement = Arrangement.SpaceAround,
             verticalAlignment = Alignment.CenterVertically,
             modifier = Modifier.fillMaxWidth()
         ) {
             Button(
                 onClick = {viewModel.start()}
             ) {
                 Text(
                     text = "Start",
                     style = MaterialTheme.typography.labelLarge,
                 )
             }
         }
     }
}

@Preview
@Composable
fun CounDownTimerPreview () {
    CounDownTimer(

    )
}