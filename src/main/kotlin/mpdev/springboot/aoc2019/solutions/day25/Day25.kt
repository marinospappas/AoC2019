package mpdev.springboot.aoc2019.solutions.day25

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day25: PuzzleSolver() {

    final override fun setDay() {
        day = 25         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = ""

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day 17 part 1")
        val icvm = ICVM(inputData[0])
        icvm.setAsciiCapable()
        icvm.useStdin()
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                log.info(">>> Type \"quit\" to quit the game <<<")
                val job = launch { icvm.runProgram() }
                icvm.waitProgram(job)
                result = "Day 25 Completed!!"
            }
        }
        // items:
        //    antenna
        //    semiconductor
        //    hypercube
        //    mouse
        return PuzzlePartSolution(1, result, elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        return PuzzlePartSolution(2, "", 0)
    }

}