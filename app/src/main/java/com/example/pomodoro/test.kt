import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.i18n.DateTimeFormatter
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun main () {

    println(create24hoursKeys())

}
@RequiresApi(Build.VERSION_CODES.O)
private val formatterDay = java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy")
@RequiresApi(Build.VERSION_CODES.O)
fun create24hoursKeys(): List<String> {
    val keys = (0..23).toList()
    val todayKey = LocalDate.now().format(formatterDay)
    val listTodaykey: MutableList<String> = mutableListOf()
    for ( key in keys) {
        listTodaykey.add(todayKey + "T" + key)
    }
    return listTodaykey
}