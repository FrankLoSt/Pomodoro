import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.model.Point
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields


fun main () {
    val regexDayHourKey = Regex("""\d{2} \d{2} \d{4}T\d{2}""")
    val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")
    val todayKey: LocalDate = LocalDate.now()
    val year = 2025


    val totalFocusOfADayInACertainYear = preferencesObj.asMap()
        .filterKeys {
            regexDayHourKey.matches(it.name)
            //return a Map that only contains keys that matches the form : "29 09 2025T0"
        }.toList()
        .groupBy { it.first.name.substringBefore("T") }
        //this will just return an empty Map if preferencesObject is empty
        .mapValues { values ->
            values.value.sumOf { pair ->
                pair.second.toString().toIntOrNull() ?: 0
            }
        }
        .filterKeys{it.contains(year.toString())}



    val listAvailableYear = totalFocusOfADayInACertainYear.keys.map{ day ->
        LocalDate.parse(day, formatterDay).year
    }.toSet()


    val totalFocusAMonth = totalFocusOfADayInACertainYear.keys
        .groupBy{days ->
            LocalDate.parse(days, formatterDay).month
        }
        .mapKeys{entry -> entry.key.value}
        .mapValues{entry -> entry.value.sumOf { day ->  totalFocusOfADayInACertainYear.getOrDefault(day, 0) }}






    val yearMonthMap: Map<Int, List<Point>> = buildMap {
        val listMonths = (1..12).toList().mapIndexed{ index, month ->
            val monthFocus = totalFocusAMonth.getOrDefault(month, 0)
             Point((index+1).toFloat(), monthFocus.toFloat())
        }
        put(year, listMonths)
    }




    //...------------------------------------

    val input = "123"
    val result: Result<Int> = runCatching { input.toInt() }
    println(result)
    result
        .onSuccess { age ->
            println("Valid age: $age")
            // Proceed with registration or next step
            nextStep(age)
        }
        .onFailure { error ->
            println("Invalid input: ${error.message}")
            // Show error message to user
            ifFalse()
        }


    val parsedEntries = preferencesObj.asMap()
        .filterKeys { regexDayHourKey.matches(it.name) }
        .mapNotNull { (key, value) ->
            val dateStr = key.name.substringBefore("T")
            val date  = runCatching { LocalDate.parse(dateStr, formatterDay) }.getOrNull()
            //`result.getOrNull()` – returns the value or `null` if failed
            date?.let { Triple(it, key.name, value.toString().toIntOrNull() ?: 0) }
        }
}

fun isLeapYear(year: Int): Boolean {
    return java.time.Year.of(year).isLeap
}


fun nextStep (age: Int) {
    println("This is the next step")
}

fun ifFalse () {
    println("If faile -> this is how handle")
}




val preferencesObj = mutablePreferencesOf(
    stringPreferencesKey("26 09 2025T15") to "10",
    stringPreferencesKey("last_active_time") to "06 10 2025T08",
    stringPreferencesKey("26 09 2025T16") to "30",
    stringPreferencesKey("26 09 2011T16") to "30",
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

