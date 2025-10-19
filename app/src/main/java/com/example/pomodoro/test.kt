import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.model.Point
import com.example.pomodoro.data.MonsterFightingHourlyFocus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields


fun main () {
    val test2 = test.associate {
        it.hour to it.focusTime
    }.filterKeys{
        it.substringBefore("T") == "2025 10 19"
    }


    println(test2)


    val hourList = List(24) { index -> index }
    val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")

    fun createHourKey(base: String, unit: Int): String {
        val padded = unit.toString().padStart(2, '0')
        return "${base}T$padded"
    }

    fun create24HoursKey(
        dateString: String,
        hourFocusList: Map<String, Int>
    ): List<Point> {
        val chartDataDay = hourList.mapIndexed { index, hour ->
            val hourKey = createHourKey(dateString, hour)
            //2025 10 19T03 or 2025 10 19T12
            Point(index.toFloat(), hourFocusList[hourKey]?.toFloat() ?: 0f)
        }
        return chartDataDay
    }

    val chartDataDay = create24HoursKey("2025 10 19", test2)

    println(chartDataDay)
}



val test = listOf(MonsterFightingHourlyFocus("2025 10 19T03", 10), MonsterFightingHourlyFocus("2025 10 19T04", 20),
    MonsterFightingHourlyFocus("2025 10 19T05", 30), MonsterFightingHourlyFocus("2025 10 19T06", 40),
    MonsterFightingHourlyFocus("2025 10 19T07", 50), MonsterFightingHourlyFocus("2025 10 19T08", 60),
    MonsterFightingHourlyFocus("2025 10 18T09"), MonsterFightingHourlyFocus("2025 10 18T10"),
    MonsterFightingHourlyFocus("2025 10 17T11"), MonsterFightingHourlyFocus("2025 10 16T03"))

