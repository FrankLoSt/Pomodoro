package com.example.pomodoro.ui.countdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.R

@Composable
fun CountDownButton (
    startCountDown:() -> Unit = {},
) {
    Button(
        onClick = {
            startCountDown()
        },
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent),
        ) {
            Image(
                painter = painterResource(R.drawable.startbut2),
                contentDescription = null,
                modifier = Modifier.size(width = 100.dp, height = 50.dp)
            )
        }
    }
}
    //Start - giveUp - take break buttons

@Preview
@Composable
fun CountDownButtonPreview () {
    CountDownButton()
}

