import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun main () {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH")
        val hourKey = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).format(formatter)
    println(hourKey)

}