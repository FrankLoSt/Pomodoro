import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*



fun main() = runBlocking {

    suspend fun countdown() {
        for (i in 10 downTo 0) {
            println("Countdown: $i")
            delay(1000)
        }
    }
    suspend fun countdown2() {
        for (i in 10 downTo 0) {
            println("Countdown2: $i")
            delay(1000)
        }
    }

    val job1 = launch{countdown()}
    val job2 = launch{countdown2()}
    //job1, job1 run concurrently
    joinAll(job1, job2)
}