import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.i18n.DateTimeFormatter
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun main () {

    val hourKey = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).toString()
    println(hourKey)
}