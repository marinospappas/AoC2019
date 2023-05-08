package mpdev.springboot.aoc2019.solutions.day09

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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
        initInputOutput()
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            val t = thread(start = true, name = "boost-prog-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            setInputValues(listOf(1L))
            t.join()
        }
        result = getOutputValues().last()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        initInputOutput()
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            val t = thread(start = true, name = "boost-prog-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            setInputValues(listOf(2L))
            t.join()
        }
        result = getOutputValues().last()
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}