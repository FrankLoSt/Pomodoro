import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.ui.screen2.ViewModelChart
import com.google.api.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import com.madrapps.plot.line.DataPoint

@RequiresApi(Build.VERSION_CODES.O)
fun main () {
    val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")

    val dayData:Map<String, List<Pair<Preferences.Key<*>, Any>>> = preferencesObj.asMap()
        .filterKeys{
            regexDayHourKey.matches(it.name)
            //return a Map that only contains keys that matches the form : "29 09 2025T0"
        }.toList()
        .groupBy{it.first.name.substringBefore("T")}

    val totalFocusDayData: Map<String, Int> = dayData.mapValues{ values ->
        values.value.sumOf { pair -> pair.second.toString().toIntOrNull()?: 0 }
        //type Any -> to Int
    }
    val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
    val listDays: List<LocalDate> = totalFocusDayData.map{LocalDate.parse(it.key, formatter)}

    val year = 2025
    val weekFields = WeekFields.ISO // Monday-based weeks
    val firstDayOfYear = LocalDate.of(year, 1, 1)
    val listWeeks: Map<Int, List<DataPoint>> = buildMap {
        listDays.map { it.get(WeekFields.ISO.weekOfYear()) }
            .toSet()
            .forEach { weekNumber ->
                val firstWeekDate =
                    firstDayOfYear.with(weekFields.weekOfYear(), weekNumber.toLong())
                val startOfWeek = firstWeekDate.with(weekFields.dayOfWeek(), 1) // Monday
                val datesInWeek = (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatter) }
                put(weekNumber, datesInWeek)
            }
    }.mapValues { entry ->
        entry.value.mapIndexed { index, date ->
            println("date: $date")
            DataPoint(index.toFloat(), totalFocusDayData[date]?.toFloat() ?: 0f
            )
        }
    }
    println(listWeeks)

}


val preferencesObj = mutablePreferencesOf(
    stringPreferencesKey("26 09 2025T15") to "10",
    stringPreferencesKey("last_active_time") to "06 10 2025T08",
    stringPreferencesKey("26 09 2025T16") to "30",
    stringPreferencesKey("26 09 2025T18") to "118",
    stringPreferencesKey("26 09 2025T19") to "52",
    stringPreferencesKey("26 09 2025T20") to "129",
    stringPreferencesKey("26 09 2025T21") to "62",
    stringPreferencesKey("27 09 2025T00") to "110",
    stringPreferencesKey("27 09 2025T01") to "65",
    stringPreferencesKey("2025-39") to "57",
    stringPreferencesKey("2025-9") to "57",
    stringPreferencesKey("2025") to "67",
    stringPreferencesKey("27 09 2025T02") to "10",
    stringPreferencesKey("27 09 2025T03") to "16",
    stringPreferencesKey("27 09 2025T13") to "11",
    stringPreferencesKey("27 09 2025T14") to "10",
    stringPreferencesKey("04 10 2025T08") to "4",
    stringPreferencesKey("2025-40") to "10",
    stringPreferencesKey("2025-10") to "10",
    stringPreferencesKey("04 10 2025T15") to "28",
    stringPreferencesKey("04 10 2025T16") to "34",
    stringPreferencesKey("04 10 2025T20") to "20",
    stringPreferencesKey("05 10 2025T20") to "30",
    stringPreferencesKey("06 10 2025T08") to "10"
)

