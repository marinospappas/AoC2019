package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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
    var result = 0L

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        initInputOutput()
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            val t = thread(start = true, name = "self-test-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            setInputValues(listOf(1L))
            t.join()
            result = getOutputValues().last()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            initInputOutput()
            val program = ICProgram(inputData[0])
            val t = thread(start = true, name = "self-test-0") {     // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            setInputValues(listOf(5L))
            t.join()
            result = getOutputValues().last()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}