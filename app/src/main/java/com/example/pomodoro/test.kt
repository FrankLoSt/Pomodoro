import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// This is the Well Pump - it creates the stream of water (data)
fun createWaterFlow(): Flow<Int> = flow {
    // The pump sends 3 "units" of water, one by one, with delays
    emit(1) // First unit of water comes out
    delay(1000) // Wait 1 second
    emit(2) // Second unit of water
    delay(1000)
    emit(3) // Third unit of water
}

fun main() = runBlocking { // This is like turning on the main water supply

    println("Calling the well operator to start the pump...")

    // You turning on your tap to collect water (collecting the Flow)
    createWaterFlow().collect { waterUnit ->
        println("--> Got water unit: $waterUnit")
    }

    println("The pump stopped. The pipe is now empty and 'cold'.")
}