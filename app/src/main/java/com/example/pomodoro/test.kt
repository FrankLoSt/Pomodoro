import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.i18n.DateTimeFormatter
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields

@RequiresApi(Build.VERSION_CODES.O)
fun main () {
    val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")
    val dayData = preferencesObj.asMap()
        .filterKeys{
            regexDayHourKey.matches(it.name)
            //return a Map that only contains keys that matches the form : "29 09 2025T0"
        }.toList()
        .groupBy{it.first.name.substringBefore("T")}
    println(dayData)
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

