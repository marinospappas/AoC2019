package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day05: PuzzleSolver() {

    final override fun setDay() {
        day = 5         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    @Volatile
    var result = 0

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.setProgramInput(1)
            icvm.waitProgram()
            result = icvm.getProgramOutput().last()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.setProgramInput(5)
            icvm.waitProgram()
            result = icvm.getProgramOutput().last()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}