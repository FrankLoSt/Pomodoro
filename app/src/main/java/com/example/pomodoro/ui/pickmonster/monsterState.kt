package com.example.pomodoro.ui.pickmonster

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.pomodoro.R

data class InitSetUpState(
    val toggleSetUp: Boolean = false,
    val updateMonster: Triple<Int, String, String> = Triple(  R.drawable._01_1, "Anxiety", "Makes everyday tasks feel overwhelming")
)