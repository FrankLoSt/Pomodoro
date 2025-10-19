package com.example.pomodoro.ui.pickmonster

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.ui.EnumScreenClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonsterViewModel @Inject constructor(
    private val monsterDataController: MonsterDataControllerImpl,
): ViewModel() {

    val initSetUpState = monsterDataController.initSetUpState

    fun updateMonsterPickedIndex(index: Int) = monsterDataController.updateMonsterPickedIndex(index)

    //suspend fun updateMonsterList () = monsterDataController.updateMonsterList()

    fun toggleSetUpPopup() = monsterDataController.toggleSetUpPopup()

    fun migrateHourFocusData() = viewModelScope.launch { monsterDataController.migrateHourFocusData()}

    fun shouldShowDrawer(screen: EnumScreenClass): Boolean {
        return screen == EnumScreenClass.PICKMONSTER
    }

}


