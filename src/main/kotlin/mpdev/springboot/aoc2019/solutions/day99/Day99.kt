package mpdev.springboot.aoc2019.solutions.day99

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day99: PuzzleSolver() {

    final override fun setDay() {
        day = 99         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = mutableListOf<Long>()

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData.first { line -> !line.startsWith("#") })
        var elapsed: Long
        result = mutableListOf()
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                icvm.setProgramInput(6)
                icvm.waitProgram(job)
                result.addAll(icvm.getProgramOutputLong())
            }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        return PuzzlePartSolution(2, "no part 2 result", 0)
    }

}