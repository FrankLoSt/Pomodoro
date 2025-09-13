package com.example.pomodoro


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun CountDownTimer (
    viewModel: ViewModelCountDown = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
     Column(
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally,
         modifier = Modifier
             .fillMaxSize()
             .padding(top = 100.dp)
     ) {
         Text(
             text = if (uiState.isStudying) stringResource(R.string.Studying) else  stringResource(R.string.Taking_a_break),
         )
         Box(
             contentAlignment = Alignment.Center,
             modifier = Modifier.size(300.dp),
         ) {
             if(!uiState.isRunning) {
                 CircularSlider(
                     modifier = Modifier.size(300.dp),
                     stroke = 40f,
                     cap = StrokeCap.Round,
                     onChange = { viewModel.onSliderChangeTesting(it) }, //Testing
                     thumbColor = Color.Red,
                     progressColor = Color.Green,
                     backgroundColor = Color.LightGray,
                     debug = false
                 )
             } else if (!uiState.isStudying && uiState.restDuration > 0 ) {
                 val progress =
                     if (uiState.initialRestDuration > 0)
                         1f - (uiState.restDuration.toFloat() / uiState.initialDuration.toFloat())
                     else 0f
                 CustomCircularProgressIndicator(
                     progress = progress,
                     modifier = Modifier.size(300.dp),
                     progressColor = Color.Red,
                     backgroundColor = Color.LightGray,
                     stroke = 40f,
                     cap = StrokeCap.Round
                 )

             } else {
                 val progress =
                     if (uiState.initialDuration > 0)
                         1f - (uiState.duration.toFloat() / uiState.initialDuration.toFloat())
                     else 0f

                 CustomCircularProgressIndicator(
                     progress = progress,
                     modifier = Modifier.size(300.dp),
                     progressColor = Color.Red,
                     backgroundColor = Color.LightGray,
                     stroke = 40f,
                     cap = StrokeCap.Round
                 )
             }
             Text(
                 text = viewModel.formatter(uiState.duration),
                 style = MaterialTheme.typography.displayLarge
             )
         }
         println("DEBUG: UI recomposed, duration = ${uiState.duration}")
         Row(
             horizontalArrangement = Arrangement.SpaceAround,
             verticalAlignment = Alignment.CenterVertically,
             modifier = Modifier.fillMaxWidth()
         ) {
             Button(
                 onClick = {viewModel.toggleStartGiveUp()}
             ) {
                 Text(
                     text = if (uiState.isRunning) "Give up" else "Start",
                     style = MaterialTheme.typography.labelLarge,
                 )
             }
         }
     }
}









@Preview
@Composable
fun CountDownTimerPreview () {
    CountDownTimer()
}