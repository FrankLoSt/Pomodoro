import com.example.pomodoro.data.MonsterFightingHourlyFocus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking


suspend fun main ()  {
    var test = MutableStateFlow(0)

    val flowTest = flow{
        emit(1)
        delay(1000L)
        emit(2)
        delay(1000L)
        emit(3)
    }
    flowTest.collect {
        test.value = it
    }
    var stateFlow = MutableStateFlow(test.value)

    println(stateFlow)
}



val test = listOf(MonsterFightingHourlyFocus("2025 10 19T03", 10), MonsterFightingHourlyFocus("2025 10 19T04", 20),
    MonsterFightingHourlyFocus("2025 10 19T05", 30), MonsterFightingHourlyFocus("2025 10 19T06", 40),
    MonsterFightingHourlyFocus("2025 10 19T07", 50), MonsterFightingHourlyFocus("2025 10 19T08", 60),
    MonsterFightingHourlyFocus("2025 10 18T09"), MonsterFightingHourlyFocus("2025 10 18T10"),
    MonsterFightingHourlyFocus("2025 10 17T11"), MonsterFightingHourlyFocus("2025 10 16T03"))

