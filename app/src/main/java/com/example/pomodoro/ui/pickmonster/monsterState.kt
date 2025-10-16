package com.example.pomodoro.ui.pickmonster

import com.example.pomodoro.R

data class InitSetUpState(
    val toggleSetUp: Boolean = false,
    val monsterPickedIndex: Int = 0,
    val monsterList: List<MonsterInfo> = listOf(
        MonsterInfo(R.drawable._20_1, "Regret", "Chains you to the past")
    )
)