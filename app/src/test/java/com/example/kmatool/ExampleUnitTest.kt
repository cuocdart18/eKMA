package com.example.kmatool

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun main() = runBlocking {
        val time = measureTimeMillis {
            val one = async { printOne() }
            val two = async { printTwo() }
            println("The answer")
            println("The answer 0 is ${one.await()}")
            println("The answer")
            println("The answer 2 is ${two.await()}")
            println("The answer")
            println("The answer 3 is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    private suspend fun printOne(): Int {
        delay(10000L)
        return 10
    }

    private suspend fun printTwo(): Int {
        delay(20000L)
        return 20
    }
}
