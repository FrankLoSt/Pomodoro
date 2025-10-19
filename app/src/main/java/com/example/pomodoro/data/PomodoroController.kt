package com.example.pomodoro.data

// --- PomodoroController.kt ---
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.example.pomodoro.ui.countdown.AppPhase
import com.example.pomodoro.ui.countdown.FocusUiState
import com.example.pomodoro.ui.countdown.RestUiState
import com.example.pomodoro.ui.countdown.TimerState
import com.example.pomodoro.ui.pickmonster.InitSetUpState
import com.example.pomodoro.ui.pickmonster.InitSetUpStateHolder
import com.example.pomodoro.ui.pickmonster.MonsterDataController

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface PomodoroController {
    val focusUiState: StateFlow<FocusUiState>

    val restUiState: StateFlow<RestUiState>


    fun setDurationMinutes(minutes: Int)
    fun setRestDurationMinutes(minutes: Int)

    fun setSessions(sessions: Int)

    fun start()
    fun breakFun()
    fun pause() // optional helper for tests\
    fun resume ()

    fun reset()
    fun formatter(durationSeconds: Int): String
}


class PomodoroControllerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope, // usually viewModelScope, -----  uses Dispatchers.Main by default
    private val monsterDataController: MonsterDataController,
    private val initSetUpState: InitSetUpStateHolder
) : PomodoroController {

    private val _focusUiState = MutableStateFlow(FocusUiState())
    override val focusUiState: StateFlow<FocusUiState> = _focusUiState.asStateFlow()

    private val _restUiState = MutableStateFlow(RestUiState())

    override val restUiState: StateFlow<RestUiState> = _restUiState.asStateFlow()



    //Create job controllers for 2 countdown
    private var studyJob: Job? = null

    //--------SET UP ---------
    override fun setDurationMinutes(minutes: Int) {
        val seconds = minutes   //remove  * 60 for testing  - minutes * 60
        _focusUiState.update { it.copy(duration = seconds, initialDuration = seconds) }
        Log.d("DEBUG", "setDurationMinutes: $seconds assigned")
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes  //remove  * 60 for testing  - minutes * 60
        _restUiState.update { it.copy(restDuration = seconds, initialRestDuration = seconds) }
        Log.d("DEBUG", "setRestDurationMinutes: $seconds assigned")
    }

    override fun setSessions(sessions: Int) {
        _focusUiState.update { it.copy(totalSessions = sessions) } //initial session = 1, it will increase by 1 when studying
        Log.d("DEBUG", "setSessions: $sessions assigned")
    }



    //------------- Count down Logic-------------
    private suspend fun countdownStudy() = coroutineScope {
        //set up

        _focusUiState.update{
            it.copy(
                timerState = TimerState.RUNNING,
                appPhrase = AppPhase.FOCUSING
            )
        }

        if( monsterDataController.getLatestById() == null) {
            val newRow = MonsterFightingDB(
                monsterName = initSetUpState.initSetUpState.value.monsterList[initSetUpState.initSetUpState.value.monsterPickedIndex].name,
                totalSessions = focusUiState.value.totalSessions,
                timestampStart = System.currentTimeMillis(),
            )
            monsterDataController.insertMonsterFightData(newRow)
        }

        while (isActive) {
            while (focusUiState.value.timerState == TimerState.PAUSED) {
                Log.d("DEBUG", "countdownStudy: pausing")
                delay(100L)
            }

            if (focusUiState.value.timerState == TimerState.STOPPED||
                focusUiState.value.appPhrase == AppPhase.FINISHED ||
                focusUiState.value.appPhrase == AppPhase.IDLE ||
                focusUiState.value.appPhrase == AppPhase.RESTING ||
                focusUiState.value.duration <= 0) break
            //Only runs when appPhase == FOCUSING, and duration > 0.
            delay(1000L)

            if(focusUiState.value.timerState == TimerState.RUNNING && focusUiState.value.appPhrase == AppPhase.FOCUSING) {
                settingsRepository.saveHourlyFocusDuration(1)

                val ticks = monsterDataController.saveTick(1)

                if(ticks == focusUiState.value.initialDuration) {
                    val latestRow = monsterDataController.getLatestById()
                    Log.d("ROOM", "Latest Row: $latestRow")
                    val updated = latestRow?.copy(
                        totalFocusTime = latestRow.totalFocusTime + ticks,
                        sessionsCompleted = focusUiState.value.sessions,
                    )

                    if (updated != null) {
                        monsterDataController.updateMonsterFightData(updated)
                        Log.d("ROOM", "updateMonsterFightData: called")
                    }

                }
            }
            //only save when isPause = false, app is running and users are studying

            _focusUiState.update { it.copy(duration = if (focusUiState.value.timerState == TimerState.PAUSED) it.duration else (it.duration - 1).coerceAtLeast(0)) }

            Log.d("DEBUG", "countdownStudy: ${focusUiState.value.duration}")
        }
        Log.d("DEBUG", "countdownStudy: study finished")
        _focusUiState.update{
            it.copy(
                duration = it.initialDuration,
            )
        }
        Log.d("DEBUG", "countdownStudy: duration reset")
    } //countdown for study session


    private suspend fun countdownRest() = coroutineScope {

        _focusUiState.update{
            it.copy(
                appPhrase = AppPhase.RESTING,
                timerState = TimerState.RUNNING
            )
        }


        while (isActive) {
            while (focusUiState.value.timerState == TimerState.PAUSED) {
                Log.d("DEBUG", "countdownRest: pausing")
                delay(100L)
            }

            if (
                focusUiState.value.timerState  == TimerState.STOPPED ||
                restUiState.value.restDuration <= 0 ||
                focusUiState.value.appPhrase  == AppPhase.FINISHED ||
                focusUiState.value.appPhrase == AppPhase.FOCUSING ||
                focusUiState.value.appPhrase == AppPhase.IDLE
                ) { break }

            delay(1000L)

            if(focusUiState.value.timerState == TimerState.RUNNING && focusUiState.value.appPhrase == AppPhase.RESTING) {

                val ticks = monsterDataController.saveTick(1)

                if(ticks == restUiState.value.initialRestDuration){
                    val latestRow = monsterDataController.getLatestById()
                    Log.d("ROOM", "Latest Row: $latestRow")
                    val updated = latestRow?.copy(
                        totalRestTime = latestRow.totalRestTime + ticks,
                    )

                    if(updated != null) {
                        monsterDataController.updateMonsterFightData(updated)
                    }
                }
            }

            _restUiState.update { it.copy(restDuration = if (focusUiState.value.timerState == TimerState.PAUSED) it.restDuration else (it.restDuration - 1).coerceAtLeast(0)) }
                                                       //if isPause -> no update
            Log.d("DEBUG", "countdownRest: ${restUiState.value.restDuration}")
        }
        _restUiState.update{
            it.copy(
                restDuration = it.initialRestDuration,
            )
        }
    } //countdown for rest session

    override fun start () {

        studyJob?.cancel()
        Log.d("DEBUG", "start: start() runs")

        _focusUiState.update { it.copy(
            timerState = TimerState.RUNNING,
            appPhrase = AppPhase.FOCUSING,
        ) }

        //Every time users press Start -> create a rew ROW
        val newRow = MonsterFightingDB(
            monsterName = initSetUpState.initSetUpState.value.monsterList[initSetUpState.initSetUpState.value.monsterPickedIndex].name,
            totalSessions = focusUiState.value.totalSessions,
            timestampStart = System.currentTimeMillis(),
        )


        studyJob = scope.launch {
            monsterDataController.insertMonsterFightData(newRow)
            //delay(100L) WTF is this for?
            while (focusUiState.value.sessions <= focusUiState.value.totalSessions) {

                countdownStudy() //before running countdown, make sure isRunning = true, duration > 0, and isStudying = true.

                if(focusUiState.value.sessions == focusUiState.value.totalSessions) break // at the last session, break

                countdownRest() // isStudying = false because users are not studying, they are taking a break.

                _focusUiState.update {
                    it.copy(
                        sessions = it.sessions + 1,
                    )
                } // after finish studying and resting => session + 1 and start studying again
                Log.d("DEBUG", "Number of sessions: ${focusUiState.value.sessions}/${focusUiState.value.totalSessions}")
            }

            val latestRow = monsterDataController.getLatestById()

            val updated = latestRow?.copy(
                timestampEnd = System.currentTimeMillis(),
                status = true
            )
            if(updated != null) {
                monsterDataController.updateMonsterFightData(updated)
                Log.d("ROOM", "Monster fight data updated ${updated}")
            }

            monsterDataController.migrateHourFocusData()

            _focusUiState.update { it.copy(appPhrase = AppPhase.FINISHED) } // if appPhase == FINISHED ->
            Log.d("DEBUG", "start: start() ends")
        }
    }

     override  fun reset(){
        studyJob?.cancel()

        _focusUiState.update {
            it.copy(
                appPhrase = AppPhase.IDLE,
                timerState =  TimerState.STOPPED,
                duration = it.initialDuration,
                totalSessions = 1,
                sessions = 1,
            )
        }
        _restUiState.update {
            it.copy(
                restDuration = it.initialRestDuration,
            )
        }
        Log.d("reset", "reset: reset done!")
    }

     fun toggleisFinished () {
        reset()
     }


    //----BREAK - PAUSE - RESUME LOGIC -----
    override fun breakFun () {
        Log.d("DEBUG", "breakFun: breakFun() runs")
        reset() //cancel all jobs
        scope.launch {
            val ticks = monsterDataController.saveTick(0)
            val latestRow = monsterDataController.getLatestById()
            val update = latestRow?.copy(
                status = false,
                timestampEnd = System.currentTimeMillis(),
                totalFocusTime = latestRow.totalFocusTime + ticks,
            )
            if(update != null) {
                monsterDataController.updateMonsterFightData(update)
            }
            monsterDataController.migrateHourFocusData()
            Log.d("ROOM", "latest Row = ${monsterDataController.getLatestById()}")
        }
        //Update
    }

    override fun pause() {
        Log.d("DEBUG", "pause: pause() runs")
        _focusUiState.update { it.copy(timerState = TimerState.PAUSED) }
    }
    override fun resume () {
        Log.d("DEBUG", "resume: resume() runs")

        _focusUiState.update { it.copy(timerState = TimerState.RUNNING) }
    }


    override fun formatter(durationSeconds: Int): String {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
