package com.example.pomodoro.ui

import com.example.pomodoro.ui.pickmonster.MonsterState


data class MonsterUIState(
    val state: MonsterState,
    val onMonsterPicked: (Int) -> Unit
)

data class SessionConfig(
    val setDurationMinutes: (Int) -> Unit,
    val setRestDurationMinutes: (Int) -> Unit,
    val setSessions: (Int) -> Unit,
    val setTag: (String) -> Unit,

    val listFocusDuration: List<Int>,
    val listRestDuration: List<Int>,
    val listSessions: List<Int>,

    val isLongBreak: Boolean,
    val toggleLongBreak: (Boolean) -> Unit,
    val setLongBreakMinutes: (Int) -> Unit,
    val setLongBreakAfter: (Int) -> Unit
)

data class SheetControl(
    val toggleSetUpPopup: () -> Unit,
    val confirmBut: () -> Unit
)