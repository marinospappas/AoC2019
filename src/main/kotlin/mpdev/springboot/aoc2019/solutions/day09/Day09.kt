package mpdev.springboot.aoc2019.solutions.day09

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVMc
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day09: PuzzleSolver() {

    final override fun setDay() {
        day = 9         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0L

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVMc(inputData[0])
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                icvm.setProgramInput(1)
                icvm.waitProgram(job)
                result = icvm.getProgramOutputLong().last()
            }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVMc(inputData[0])
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                icvm.setProgramInput(2)
                icvm.waitProgram(job)
                result = icvm.getProgramOutputLong().last()
            }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}