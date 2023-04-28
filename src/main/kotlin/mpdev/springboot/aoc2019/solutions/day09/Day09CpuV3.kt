package mpdev.springboot.aoc2019.solutions.day09

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.big
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.math.BigInteger
import kotlin.system.measureTimeMillis

@Component
@Order(9)
class Day09CpuV3(@Autowired var inputProcessor: InputProcessor09): PuzzleSolver() {

    override fun setDay() {
        day = 9         ////// update this when a puzzle solver for a new day is implemented
    }

    private lateinit var program: Program
    var result = BigInteger.valueOf(0)

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            InputOutput.input = 1.big()
            program.run()
            result = InputOutput.output.last()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            InputOutput.input = 2.big()
            program.run()
            result = InputOutput.output.last()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}