package mpdev.springboot.aoc2019.solutions.day09

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
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
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.setProgramInput(1)
            icvm.waitProgram()
            result = icvm.getProgramOutputLong().last()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.setProgramInput(2)
            icvm.waitProgram()
            result = icvm.getProgramOutputLong().last()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}