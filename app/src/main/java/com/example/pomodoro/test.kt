import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.i18n.DateTimeFormatter
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields

@RequiresApi(Build.VERSION_CODES.O)
fun main () {
     trackWeekYear()
}
@RequiresApi(Build.VERSION_CODES.O)
fun trackWeekYear () {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek  // e.g., MONDAY, TUESDAY
    val dayName = dayOfWeek.name     // "MONDAY"
    val dayIndex = dayOfWeek.value   // 1 (Monday) to 7 (Sunday)
    val month = today.month.name
    val week = today.get(WeekFields.ISO.weekOfWeekBasedYear())
    val weekOfYear = today.get(WeekFields.ISO.weekOfYear())  // e.g., 39
    val year = today.year
    val monthKey = LocalDateTime.now().month.value.toString() + LocalDateTime.now().year.toString()
    println(" $today is $dayOfWeek in the week $week of the month $month in the year $year \n $monthKey  ")
} //this is only for testing
