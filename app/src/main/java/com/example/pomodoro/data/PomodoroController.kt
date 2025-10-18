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



   private  val _monsterFightingDb = MutableStateFlow(MonsterFightingDB())
    val monsterFightingDB: StateFlow<MonsterFightingDB> = _monsterFightingDb.asStateFlow()


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



                if (ticks == focusUiState.value.initialDuration) {

                    Log.e("ROOM", "current monster name: ${initSetUpState.value.monsterList[_initSetUpState.value.monsterPickedIndex].name}")
                    _monsterFightingDb.update {
                        it.copy(
                            monsterName = initSetUpState.value.monsterList[_initSetUpState.value.monsterPickedIndex].name,
                            totalSessions = focusUiState.value.totalSessions,
                            sessionsCompleted = focusUiState.value.sessions,
                            totalFocusTime = monsterFightingDB.value.totalFocusTime + ticks, //total time recorded + tick recorded
                        )
                    }

                    Log.e("ROOM", "monsterFightingDB: ${monsterFightingDB.value}")
                    val existing = monsterDataController.getAllMonsterFightData()
                        .any { it.id == monsterFightingDB.value.id }

                    if (!existing) {
                        //if not in the list, insert
                        Log.e("ROOM", "insert called")
                        monsterDataController.insertMonsterFightData(monsterFightingDB.value)
                    } else {
                        monsterDataController.updateMonsterFightData(monsterFightingDB.value)
                        Log.d(
                            "ROOM",
                            "Data from ROOM: ${monsterDataController.getAllMonsterFightData()}"
                        )
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
                ) {
                Log.e("DEBUG", "countdownRest: BREAK \n timerState = ${focusUiState.value.timerState} \n restDuration = ${restUiState.value.restDuration} \n appPhrase = ${focusUiState.value.appPhrase}")
                break
            }

            delay(1000L)

            _restUiState.update { it.copy(restDuration = if (focusUiState.value.timerState == TimerState.PAUSED) it.restDuration else (it.restDuration - 1).coerceAtLeast(0)) }
                                                       //if isPause -> no update
            Log.d("DEBUG", "countdownRest: ${restUiState.value.restDuration}")
        }
        Log.d("DEBUG", "countdownRest: break finished")
        _restUiState.update{
            it.copy(
                restDuration = it.initialRestDuration,
            )
        }
        Log.d("DEBUG", "countdownRest: restDuration reset")
    } //countdown for rest session

    override fun start () {

        studyJob?.cancel()
        Log.d("DEBUG", "start: start() runs")

        _focusUiState.update { it.copy(
            timerState = TimerState.RUNNING,
            appPhrase = AppPhase.FOCUSING,
        ) }

        studyJob = scope.launch {
            delay(100L)
            while (focusUiState.value.sessions <= focusUiState.value.totalSessions) {

                countdownStudy() //before running countdown, make sure isRunning = true, duration > 0, and isStudying = true.

                if(focusUiState.value.sessions == focusUiState.value.totalSessions) break // at the last session, break

                countdownRest() // isStudying = false because users are not studying, they are taking a break.

                _focusUiState.update {
                    it.copy(
                        sessions = it.sessions + 1, //
                    )
                } // after finish studying and resting => session + 1 and start studying again
                Log.d("DEBUG", "Number of sessions: ${focusUiState.value.sessions}/${focusUiState.value.totalSessions}")
            }

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
