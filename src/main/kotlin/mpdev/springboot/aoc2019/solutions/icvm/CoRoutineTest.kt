package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("CoRoutineTest")


fun main() {
    val channel = Channel<Int>()
    val job1: Job
    val job2: Job
    val job3: Job

    runBlocking {
        job1 = launch { task1(channel) }
        println("launched task1 ${job1.isActive}")
        job2 = launch { task2(channel) }
        println("launched task2 ${job2.isActive}")
        job3 = launch { task3(channel) }
        println("launched task3 ${job3.isActive}")
    }
    println("end ${job1.isActive} ${job2.isActive} ${job3.isActive}")
    println("${job1.isCompleted} ${job2.isCompleted} ${job3.isCompleted}")
}

 suspend fun task1(channel: Channel<Int>) {
     log.info("task 1 started")
     listOf(1,2,3,4,5,0).forEach {
         delay(500)
         channel.send(it * it)
     }
     log.info("task 1 completed")
 }

suspend fun task2(channel: Channel<Int>) {
    log.info("task 2 started")
    delay(250)
    listOf(1001,1002,1003,1004,1005).forEach {
        delay(500)
        channel.send(it)
    }
    log.info("task 2 completed")
}

suspend fun task3(channel: Channel<Int>) {
    log.info("task 3 started")
    delay(10000)
    var i: Int
    while (true) {
        i = channel.receive()
        println("received $i")
        if (i == 0)
            return
    }
}

