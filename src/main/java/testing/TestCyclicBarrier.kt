package testing

import java.util.*
import java.util.concurrent.CyclicBarrier

fun main() {
    val createdValues = mutableListOf<Int>()
    /**
     *  create a cyclic barrier that waits for 3 threads to finish their jobs, and after that,
     *  prints the sum of all the values.
     */
    val cyclicBarrier = CyclicBarrier(3) {
        println("Sum of all values is ${createdValues.sum()}")
    }

    val threads = 1.rangeTo(3).map {
        Thread {
            Thread.sleep(5000)
            val int = Random().nextInt(500)
            createdValues.add(int) //add a value to the list after 500ms
            println("Thread $it added $int")
            cyclicBarrier.await()
        }.apply {
            start()
        }
    }
    threads.forEach { thread ->
        thread.join()
    }

}