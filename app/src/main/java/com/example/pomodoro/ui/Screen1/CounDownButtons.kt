package com.example.pomodoro.ui.Screen1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

