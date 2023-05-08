package mpdev.springboot.aoc2019.solutions.day25

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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
        initInputOutput(stdin = true, stdout = true)
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            thread(start = true, name = "text-game-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }.join()
        }
        if (getOutputValues(clearChannel = false).isNotEmpty())
            result = getOutputValues().last().toString()
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