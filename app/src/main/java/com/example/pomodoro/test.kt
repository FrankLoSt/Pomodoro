import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.model.Point
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields


fun main () {
    val regexDayHourKey = Regex("""\d{2} \d{2} \d{4}T\d{2}""")
    val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")
    val todayKey: LocalDate = LocalDate.now()

    val totalFocusOfADay = preferencesObj.asMap()
        .filterKeys {
            regexDayHourKey.matches(it.name)
            //return a Map that only contains keys that matches the form : "29 09 2025T0"
        }.toList()
        .groupBy { it.first.name.substringBefore("T") }
        //this will just return an empty Map if preferencesObject is empty
        .mapValues { values ->
            values.value.sumOf{
                    pair ->
                pair.second.toString().toIntOrNull() ?:0}
        }


    val listDays: List<LocalDate> = totalFocusOfADay.map{LocalDate.parse(it.key, formatterDay)}
    println(listDays)

    val firstDayOfYear = LocalDate.of(todayKey.year, 1, 1)

    val listMonths = listDays.map { it.monthValue }.toSet().toList().map{ Month.of(it)}
    println(listMonths)

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

