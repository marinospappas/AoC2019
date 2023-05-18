package mpdev.springboot.aoc2019.solutions.day23

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.*
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day23: PuzzleSolver() {

    val NUMBER_OF_NODES = 50

    final override fun setDay() {
        day = 23         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVMMultipleInstancesc(inputData[0], ioMode = IOMode.NETWORKED)
        repeat(NUMBER_OF_NODES-1) { icvm.cloneInstance(IOMode.NETWORKED) }
        val elapsed = measureTimeMillis {
            runBlocking {
                // set network address
                repeat(NUMBER_OF_NODES) { icvm.setInstanceInput(it, it) }
                // boot all network nodes
                val jobs = Array(NUMBER_OF_NODES) {
                    launch { icvm.runInstance(it) }
                }
                // and wait until 255 address is detected
                while(NetworkIo.getNatPacket() == null)
                    delay(2)
                // stop all coroutines
                repeat(NUMBER_OF_NODES) { jobs[it].cancel() }
                repeat(NUMBER_OF_NODES) { icvm.waitInstance(it, jobs[it]) }
            }
        }
        result = NetworkIo.getNatPacket()!!.valueY.toInt()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVMMultipleInstancesc(inputData[0], ioMode = IOMode.NETWORKED)
        repeat(NUMBER_OF_NODES-1) { icvm.cloneInstance(IOMode.NETWORKED) }
        val elapsed = measureTimeMillis {
            runBlocking {
                // set network address
                repeat(NUMBER_OF_NODES) { icvm.setInstanceInput(it, it) }
                // boot all network nodes
                val jobs = Array(NUMBER_OF_NODES) {
                    launch { icvm.runInstance(it) }
                }
                // launch the NAT monitor
                val jobNat = launch { natCoRoutine() }
                // and wait until 2 packets of the same value are sent to node 0
                while(!NetworkIo.sentSameValueTo0TwiceInARow())
                    delay(2)
                // stop all coroutines
                repeat(NUMBER_OF_NODES) { jobs[it].cancel() }
                jobNat.cancel()
                log.info("NAT coroutine completed")
                log.info("Y values sent ot node 0 {}", NetworkIo.getSentToNode0().toString())
                repeat(NUMBER_OF_NODES) { icvm.waitInstance(it, jobs[it]) }
            }
        }
        result = NetworkIo.getSentToNode0().last().toInt()
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    suspend fun natCoRoutine() {
        log.info("started NAT coroutine")
        while (true) {
            delay(50)
            if (AbstractICVMc.threadTable.none { !it.isIdle }) {
                log.info("Nat sending packet to node 0 {}", NetworkIo.getNatPacket())
                NetworkIo.sendNatPacketTo0()
            }
        }
    }

}