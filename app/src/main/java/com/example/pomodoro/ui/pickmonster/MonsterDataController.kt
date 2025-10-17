package com.example.pomodoro.ui.pickmonster

import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.core.DataStore
import com.example.pomodoro.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

data class MonsterInfo (
    val imageId: Int,
    val name: String,
    val description: String,
    )




interface MonsterDataController {
    fun saveMonsterFightingData (monster: String)
}
@Singleton
class MonsterDataControllerImpl @Inject constructor(
    scope: CoroutineScope
): MonsterDataController {

/* init{
     try {
         scope.launch { updateMonsterList()}
     } catch(
         e: Exception
     ) {
         e.printStackTrace()
         Log.e("DEBUG", "error in MonsterDataControllerImpl")
     }


 }
 */

 private val _initSetUpState = MutableStateFlow(InitSetUpState())
 val initSetUpState: StateFlow<InitSetUpState> = _initSetUpState.asStateFlow()

 val monsterList: List<MonsterInfo> = listOf(
     MonsterInfo(R.drawable.warrior2, "Uncontrolled rage", "make you make impulsive and regretful decisions, breaking your relationships with your beloved people"),
     MonsterInfo(imageId = R.drawable.treemonster, "Bed rot", "rotting your future and health"),
     MonsterInfo(R.drawable.spider, "Anxiety", "Makes everyday tasks feel overwhelming"),
     MonsterInfo(R.drawable.monster1, "Porn addiction", "Drain your energy and destroy your relationship"),
     MonsterInfo(R.drawable._01_1, "Anxiety", "Makes everyday tasks feel overwhelming"),
     MonsterInfo(R.drawable._01_2, "Loneliness", "Leads to isolation and low self-worth"),
     MonsterInfo(R.drawable._02_2, "Burnout", "Kills motivation and joy in learning"),
     MonsterInfo(R.drawable._03_2, "Comparison", "Breeds insecurity through social media"),
     MonsterInfo(R.drawable._04_1, "Rejection", "Shakes confidence and self-image"),
     MonsterInfo(R.drawable._03_3, "Pressure", "Creates fear of failure and perfectionism"),
     MonsterInfo(R.drawable._06_2, "Procrastination", "Delays growth and builds guilt"),
     MonsterInfo(R.drawable._07_2, "Identity", "Confuses self-understanding and belonging"),
     MonsterInfo(R.drawable._08_2, "Addiction", "Distracts from goals and relationships"),
     MonsterInfo(R.drawable._09_2, "Bullying", "Damages trust and emotional safety"),
     MonsterInfo(R.drawable._10_2, "Self-Doubt", "Blocks ambition and creativity"),
     MonsterInfo(R.drawable._12_1, "Financial Stress", "Limits opportunity and causes anxiety"),
     MonsterInfo(R.drawable._07_3, "Overthinking", "Paralyzes decision-making"),
     MonsterInfo(R.drawable._13_2, "Imposter", "Makes success feel undeserved"),
     MonsterInfo(R.drawable._08_3, "Neglect", "Leaves emotional needs unmet"),
     MonsterInfo(R.drawable._11_1, "Fear", "Prevents risk-taking and growth"),
     MonsterInfo(R.drawable._14_1, "Toxic Positivity", "Invalidates real emotions"),
     MonsterInfo(R.drawable._15_1, "Distraction", "Scatters focus and productivity"),
     MonsterInfo(R.drawable._16_3, "Insecurity", "Erodes confidence and self-love"),
     MonsterInfo(R.drawable._14_3, "Perfectionism", "Turns effort into self-criticism"),
     MonsterInfo(R.drawable._18_2, "Isolation", "Disconnects from support systems"),
     MonsterInfo(R.drawable._19_2, "Uncertainty", "Creates anxiety about the future"),
     MonsterInfo(R.drawable._20_1, "Regret", "Chains you to the past")
 )


  fun updateMonsterPickedIndex(index: Int)  {
     _initSetUpState.update { it.copy(monsterPickedIndex = index) }
 }


 fun toggleSetUpPopup() {
     _initSetUpState.update { it.copy(toggleSetUp = !it.toggleSetUp) }
 }

 /*suspend fun updateMonsterList () {
     delay(100L)
     _initSetUpState.update {
         it.copy(monsterList = monsterList)
     }
     Log.e("DEBUG", "call updateMonsterList")
 } //THIS IS ONLY FOR TESTING
  */


 override fun saveMonsterFightingData(monster: String) {
     TODO("Not yet implemented")
 }

}

