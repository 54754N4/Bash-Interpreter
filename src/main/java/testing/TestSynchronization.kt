package testing

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

private class Incrementor() {
    val sharedCounter: AtomicInteger = AtomicInteger(0)

    fun updateCounterIfNecessary(shouldIActuallyIncrement: Boolean) {
        if (shouldIActuallyIncrement) {
            sharedCounter.incrementAndGet() // the increment (++) operation is done atomically, so all threads wait for its completion
        }
    }

    fun getSharedCounter():Int {
        return sharedCounter.get()
    }
}

fun main() = runBlocking {
    val incrementor = Incrementor()
    val scope = CoroutineScope(newFixedThreadPoolContext(4, "synchronizationPool")) // We want our code to doWork on 4 threads
    scope.launch {
        val coroutines = 1.rangeTo(1000).map {
            //create 1000 coroutines (light-weight threads).
            launch {
                for (i in 1..1000) { // and in each of them, increment the sharedCounter 1000 times.
                    incrementor.updateCounterIfNecessary(it % 2 == 0)
                }
            }
        }

        coroutines.forEach { corotuine ->
            corotuine.join() // wait for all coroutines to finish their jobs.
        }
    }.join()

    println("The number of shared counter is ${incrementor.getSharedCounter()}")
}