import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.i18n.DateTimeFormatter
import androidx.datastore.preferences.core.Preferences
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
    val weekStart = DayOfWeek.MONDAY
    val today = LocalDate.now()
    val startOfWeek = today.with(WeekFields.of(weekStart, 1).dayOfWeek(), 1)
    println(startOfWeek.get(ChronoField.DAY_OF_WEEK))
}

