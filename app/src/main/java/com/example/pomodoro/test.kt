import com.example.pomodoro.data.MonsterFightingHourlyFocus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.Calendar.DAY_OF_WEEK
import kotlin.time.Duration.Companion.days


suspend fun main ()  {
    val weekFields = WeekFields.of(java.time.DayOfWeek.MONDAY, 1)
    val dayOfWeekField: TemporalField = weekFields.dayOfWeek()
    val date: LocalDateTime = LocalDateTime.now()
    val dayOfWeek: Int = date.get(dayOfWeekField)
    println(dayOfWeek)
}



val test = listOf(MonsterFightingHourlyFocus("2025 10 19T03", 10), MonsterFightingHourlyFocus("2025 10 19T04", 20),
    MonsterFightingHourlyFocus("2025 10 19T05", 30), MonsterFightingHourlyFocus("2025 10 19T06", 40),
    MonsterFightingHourlyFocus("2025 10 19T07", 50), MonsterFightingHourlyFocus("2025 10 19T08", 60),
    MonsterFightingHourlyFocus("2025 10 18T09"), MonsterFightingHourlyFocus("2025 10 18T10"),
    MonsterFightingHourlyFocus("2025 10 17T11"), MonsterFightingHourlyFocus("2025 10 16T03"))

