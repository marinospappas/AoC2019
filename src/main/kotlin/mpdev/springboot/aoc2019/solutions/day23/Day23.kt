package mpdev.springboot.aoc2019.solutions.day23

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.day07.Day07
import mpdev.springboot.aoc2019.solutions.icvm.ICVMMultipleInstancesc
import mpdev.springboot.aoc2019.solutions.icvm.IOMode
import mpdev.springboot.aoc2019.solutions.icvm.NetworkIo
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
        repeat(NUMBER_OF_NODES-1) { _ -> icvm.cloneInstance(IOMode.NETWORKED)}
        val elapsed = measureTimeMillis {
            runBlocking {
                // set network address
                repeat(NUMBER_OF_NODES) { icvm.setInstanceInput(it, it) }
                // boot all network nodes
                val jobs = Array(NUMBER_OF_NODES) {
                    launch { icvm.runInstance(it) }
                }
                // and wait until 255 address is detected
                while(NetworkIo.getBroadcastQueue().isEmpty())
                    delay(2)
                // stop all coroutines
                repeat(NUMBER_OF_NODES) { jobs[it].cancel() }
                repeat(NUMBER_OF_NODES) { icvm.waitInstance(it, jobs[it]) }
            }
        }
        result = NetworkIo.getBroadcastQueue().first().valueY.toInt()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        result = 0
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}